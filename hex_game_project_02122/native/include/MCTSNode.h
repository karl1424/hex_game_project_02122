#ifndef MCTS_NODE_H
#define MCTS_NODE_H

#include <utility>
#include <vector>
#include <cmath>
#include <limits>
#include <random>
#include <algorithm>

struct Move {
    int x;
    int y;

    Move(int x = -1, int y = -1) : x(x), y(y) {}

    bool operator==(const Move& other) const {
        return x == other.x && y == other.y;
    }
};

class MCTSNode {
public:
    MCTSNode(MCTSNode* parent, Move move, int playerNumber);
    ~MCTSNode();

    MCTSNode* getParent();
    Move getMove();
    int getPlayerNumber();
    int getVisits();
    const std::vector<MCTSNode*>& getChildren();

    void selectAction(const std::vector<std::vector<int>>& board);

private:
    static constexpr double EXPLORATION_PARAMETER = 0.7;
    static constexpr double EPSILON = 1e-6;

    int visits;
    double wins;
    MCTSNode* parent;
    Move move;
    int playerNumber;
    bool expanded;
    std::vector<MCTSNode*> children;
    static std::mt19937 shared_rng;

    bool isLeaf();
    void expansion(const std::vector<std::vector<int>>& board);
    MCTSNode* selection();
    double simulation(MCTSNode* node, const std::vector<std::vector<int>>& board);
    void backPropagation(double value);
};

#endif
