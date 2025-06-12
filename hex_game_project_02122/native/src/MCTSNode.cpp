#include "MCTSNode.h"
#include "MCTSUtils.h"
#include <iostream>
#include <chrono>
#include <iostream>

static long long totalSelectionTime = 0;
static long long totalExpansionTime = 0;
static long long totalSimulationTime = 0;
static long long totalBackpropagationTime = 0;
static int runCount = 0;


std::mt19937 MCTSNode::shared_rng(std::random_device{}());

MCTSNode::MCTSNode(MCTSNode* parent, Move move, int playerNumber)
    : parent(parent),
      move(move),
      playerNumber(playerNumber),
      expanded(false),
      visits(0),
      wins(0) {}


void MCTSNode::selectAction(const std::vector<std::vector<int>>& board) {
    using namespace std::chrono;

    auto start = high_resolution_clock::now();

    std::vector<MCTSNode*> visited;
    MCTSNode* current = this;
    visited.push_back(current);

    // --- Selection ---
    auto selStart = high_resolution_clock::now();
    while (!current->isLeaf()) {
        current = current->selection();
        visited.push_back(current);
    }
    auto selEnd = high_resolution_clock::now();
    totalSelectionTime += duration_cast<microseconds>(selEnd - selStart).count();

    // --- Expansion ---
    if (!current->expanded) {
        auto expStart = high_resolution_clock::now();
        current->expansion(board);
        current->expanded = true;
        auto expEnd = high_resolution_clock::now();
        totalExpansionTime += duration_cast<microseconds>(expEnd - expStart).count();
    }

    // --- Simulation ---
    auto simStart = high_resolution_clock::now();
    MCTSNode* nodeToRollout = current;
    double value = simulation(nodeToRollout, board);
    auto simEnd = high_resolution_clock::now();
    totalSimulationTime += duration_cast<microseconds>(simEnd - simStart).count();

    // --- Backpropagation ---
    auto backStart = high_resolution_clock::now();
    for (MCTSNode* node : visited) {
        node->backPropagation(value);
    }
    auto backEnd = high_resolution_clock::now();
    totalBackpropagationTime += duration_cast<microseconds>(backEnd - backStart).count();

    runCount++;
}

void MCTSNode::printTimingStats() {
    std::cout << "After " << runCount << " runs (times in µs):\n";
    std::cout << "Avg Selection Time:       " << (double)totalSelectionTime / runCount << " µs\n";
    std::cout << "Avg Expansion Time:       " << (double)totalExpansionTime / runCount << " µs\n";
    std::cout << "Avg Simulation Time:      " << (double)totalSimulationTime / runCount << " µs\n";
    std::cout << "Avg Backpropagation Time: " << (double)totalBackpropagationTime / runCount << " µs\n";

    long long total = totalSelectionTime + totalExpansionTime + totalSimulationTime + totalBackpropagationTime;
    std::cout << "Avg Total Time Per Iteration: " << (double)total / runCount << " µs\n";
}

bool MCTSNode::isLeaf() {
    return children.empty();
}

void MCTSNode::expansion(const std::vector<std::vector<int>>& board) {
    const std::vector<Move>& availableMoves = getAvailableMoves(board);  // Avoid copy
    children.reserve(availableMoves.size());

    int nextPlayer = playerNumber == 1 ? 2 : 1;
    for (const auto& move : availableMoves) {
        children.push_back(new MCTSNode(this, move, nextPlayer));
    }
}

MCTSNode* MCTSNode::selection() {
    MCTSNode* selected = nullptr;
    double bestValue = -std::numeric_limits<double>::infinity();

    const double logVisits = std::log(static_cast<double>(this->visits + 1));

    std::uniform_real_distribution<double> noiseDist(0.0, EPSILON);

    for (MCTSNode* child : children) {
        double childVisits = static_cast<double>(child->visits) + EPSILON;
        double exploitationTerm = child->wins / childVisits;

        double explorationTerm = EXPLORATION_PARAMETER * std::sqrt(logVisits / childVisits);
        explorationTerm += noiseDist(shared_rng);

        double uctScore = exploitationTerm + explorationTerm;

        if (uctScore > bestValue) {
            bestValue = uctScore;
            selected = child;
        }
    }

    return selected;
}

double MCTSNode::simulation(MCTSNode* node, const std::vector<std::vector<int>>& originalBoard) {
    std::vector<MCTSNode*> pathToRoot;
    MCTSNode* current = node;
    std::vector<std::vector<int>> board = originalBoard;

    while (current->parent != nullptr) {
        pathToRoot.push_back(current);
        current = current->parent;
    }
    std::reverse(pathToRoot.begin(), pathToRoot.end());

    for (MCTSNode* pathNode : pathToRoot) {
        if (!(pathNode->move == Move(-1, -1)) && pathNode->parent != nullptr) {
            int x = pathNode->move.x;
            int y = pathNode->move.y;
            int player = pathNode->parent->playerNumber;
            board[x][y] = player;
        }
    }

    int currentPlayer = node->playerNumber;
    int winner = 0;

    std::vector<Move> availableMoves = getAvailableMoves(board);
    int totalMoves = availableMoves.size();
    int moveCount = 0;

    while (moveCount < totalMoves) {
        std::uniform_int_distribution<int> dist(0, availableMoves.size() - 1);
        auto move = availableMoves[dist(MCTSNode::shared_rng)];
        board[move.x][move.y] = currentPlayer;

        auto it = std::find(availableMoves.begin(), availableMoves.end(), move);
        if (it != availableMoves.end()) {
            availableMoves.erase(it);
        }
        
        moveCount++;
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }
    
    for (int i = 0; i < board.size(); ++i) {
        if (board[i][0] == 2 && checkWinningMove(Move(i, 0), board, 2)) {
            winner = 2;
            break;
        }
        winner = 1;
    }

    return (winner == this->playerNumber) ? 1.0 : 0.0;
}

void MCTSNode::backPropagation(double value) {
    visits++;
    wins += value;
}

MCTSNode* MCTSNode::getParent() {
    return parent;
}

Move MCTSNode::getMove() {
    return move;
}

int MCTSNode::getPlayerNumber() {
    return playerNumber;
}

const std::vector<MCTSNode*>& MCTSNode::getChildren() {
    return children;
}

int MCTSNode::getVisits() {
    return visits;
}

MCTSNode::~MCTSNode() {
    for (MCTSNode* child : children) {
        delete child;
    }
}

