#ifndef MCTS_UTILS_H
#define MCTS_UTILS_H

#include "MCTSNode.h"
#include <vector>
#include <queue>

std::vector<Move> getAvailableMoves(const std::vector<std::vector<int>> &board);
bool checkWinningMove(Move move, std::vector<std::vector<int>> board, int currentPlayer);
bool exploreNeighbors(const Move &start, const std::vector<std::vector<int>> &board, int currentPlayer);

#endif
