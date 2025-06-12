#include "MCTS.h"
#include <chrono>  // hvis det ikke allerede er inkluderet
#include <iostream>



MCTS::MCTS(std::vector<std::vector<int>> board, int playerNumber, int iterations)
    : size(board.size()),
      iterations(iterations),
      playerNumber(playerNumber),
      board(board) {}

MCTS::~MCTS() {
    delete root;
}

std::pair<int, int> MCTS::runAlgorithm() {
    using namespace std::chrono;

    auto startTime = high_resolution_clock::now();

    Move winningMove = findWinningMove();
    if (!(winningMove == Move(-1, -1))) return {winningMove.x, winningMove.y};

    root = new MCTSNode(nullptr, Move(), playerNumber);

    for (int i = 0; i < iterations; ++i) {
        root->selectAction(board);
    }

    //MCTSNode::printTimingStats();

    const auto& children = root->getChildren();
    MCTSNode* bestChild = nullptr;
    int mostVisits = -1;

    for (MCTSNode* child : children) {
        if (child->getVisits() > mostVisits) {
            mostVisits = child->getVisits();
            bestChild = child;
        }
    }

    Move bestMove = (-1, -1);
    if (bestChild != nullptr) {
        bestMove = bestChild->getMove();
    }

    auto endTime = high_resolution_clock::now();
    auto duration = duration_cast<milliseconds>(endTime - startTime).count();

/*     std::cout << "MCTS completed " << iterations << " iterations in "
              << duration << " ms\n"; */

    return {bestMove.x, bestMove.y};
}


Move MCTS::findWinningMove() {
    std::vector<Move> availableMoves = getAvailableMoves(board);

    for (const auto& move : availableMoves) {
        if (checkWinningMove(move, board, playerNumber)) {
            return move;
        }
    }

    return Move(-1, -1);
}
