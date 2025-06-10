#ifndef MCTS_H
#define MCTS_H

#include "MCTSNode.h"
#include "MCTSUtils.h"
#include <utility>
#include <random>
#include <unordered_map>
#include <vector>

class MCTSNode;

class MCTS {
public:
    MCTS(std::vector<std::vector<int>>, int, int);
    ~MCTS();

    std::pair<int, int> runAlgorithm();

private:
    int size;
    int iterations;
    int playerNumber;
    std::vector<std::vector<int>> board;
    Move findWinningMove();
    MCTSNode* root = nullptr;
};

#endif
