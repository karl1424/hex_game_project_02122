#include "MCTSNode.h"
#include "MCTSUtils.h"
#include <iostream>

std::mt19937 MCTSNode::shared_rng(std::random_device{}());

MCTSNode::MCTSNode(MCTSNode* parent, Move move, int playerNumber)
    : parent(parent),
      move(move),
      playerNumber(playerNumber),
      expanded(false),
      visits(0),
      wins(0) {}


void MCTSNode::selectAction(const std::vector<std::vector<int>>& board) {
    std::vector<MCTSNode*> visited;
    MCTSNode* current = this;
    visited.push_back(current);

    while (!current->isLeaf()) {
        current = current->selection();
        visited.push_back(current);
    }

    if (!current->expanded) {
        current->expansion(board);
        current->expanded = true;
    }

    MCTSNode* nodeToRollout = current;
    double value = simulation(nodeToRollout, board);

    for (MCTSNode* node : visited) {
        node->backPropagation(value);
    }
}


bool MCTSNode::isLeaf() {
    return children.empty();
}

void MCTSNode::expansion(const std::vector<std::vector<int>>& board) {
    std::vector<Move> availableMoves = getAvailableMoves(board);
    children.reserve(availableMoves.size());

    int nextPlayer = playerNumber == 1 ? 2 : 1;
    for (const auto& move : availableMoves) {
        auto child = new MCTSNode(this, move, nextPlayer);
        children.push_back(child);
    }
}

MCTSNode* MCTSNode::selection() {
    MCTSNode* selected = nullptr;
    double bestValue = -std::numeric_limits<double>::infinity();

    for (MCTSNode* child : children) {
        double exploitationTerm = child->wins / (child->visits + EPSILON);
        std::uniform_real_distribution<double> noiseDist(0.0, EPSILON);
        double explorationTerm = EXPLORATION_PARAMETER *
                std::sqrt(std::log(this->visits + 1) / (child->visits + EPSILON)) +
                noiseDist(shared_rng);

        double uctScore = exploitationTerm + explorationTerm;

        if (uctScore > bestValue) {
            selected = child;
            bestValue = uctScore;
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
        if (checkWinningMove(Move(i, 0), board, 2)) {
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

