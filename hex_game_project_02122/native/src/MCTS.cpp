#include "MCTS.h"


MCTS::MCTS(std::vector<std::vector<int>> board, int playerNumber, int iterations)
    : size(board.size()),
      iterations(iterations),
      playerNumber(playerNumber),
      board(board) {}

MCTS::~MCTS() {
    delete root;
}

std::pair<int, int> MCTS::runAlgorithm() {
    Move winningMove = findWinningMove();
    if (!(winningMove == Move(-1, -1))) return {winningMove.x, winningMove.y};

    root = new MCTSNode(nullptr, Move(), playerNumber);

    for (int i = 0; i < iterations; ++i) {
        root->selectAction(board);
    }

    return {0, 0};

    MCTSNode* bestChild = nullptr;
    int mostVisits = -1;

    for (MCTSNode* child : root->getChildren()) {
        if (child->getVisits() > mostVisits) {
            mostVisits = child->getVisits();
            bestChild = child;
        }
    }

    Move bestMove = (-1, -1);
    if (bestChild != nullptr) {
        bestMove = bestChild->getMove();
    }

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
