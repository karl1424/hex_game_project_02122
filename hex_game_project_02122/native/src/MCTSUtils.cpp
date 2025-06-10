#include "MCTSUtils.h"

std::vector<Move> getAvailableMoves(const std::vector<std::vector<int>>& board) {
    std::vector<Move> availableMoves;
    availableMoves.reserve(board.size() * board.size());
    for (int x = 0; x < board.size(); ++x) {
        for (int y = 0; y < board.size(); ++y) {
            if (board[x][y] == 0) {
                availableMoves.emplace_back(Move(x, y));
            }
        }
    }
    return availableMoves;
}

bool checkWinningMove(Move move, std::vector<std::vector<int>> board, int currentPlayer) {
    int x = move.x;
    int y = move.y;
    int originalState = board[x][y];
    board[x][y] = currentPlayer;
    bool isWinning = exploreNeighbors(move, board, currentPlayer);
    board[x][y] = originalState;
    return isWinning;
}

bool exploreNeighbors(const Move& start, const std::vector<std::vector<int>>& board, int currentPlayer) {
    int directionsX[6] = { 0, 0, -1, 1, -1, 1 };
    int directionsY[6] = { 1, -1, 0, 0, 1, -1 };

    std::queue<Move> queue;
    std::vector<std::vector<bool>> visited(board.size(), std::vector<bool>(board.size(), false));

    queue.push(start);
    visited[start.x][start.y] = true;

    bool reachedStartEdge = false;
    bool reachedEndEdge = false;

    while (!queue.empty()) {
        auto current = queue.front();
        queue.pop();

        int x = current.x;
        int y = current.y;

        if (currentPlayer == 2) {
            if (y == 0) reachedStartEdge = true;
            if (y == board.size() - 1) reachedEndEdge = true;
        } else {
            if (x == 0) reachedStartEdge = true;
            if (x == board.size() - 1) reachedEndEdge = true;
        }

        if (reachedStartEdge && reachedEndEdge) return true;

        for (int i = 0; i < 6; ++i) {
            int nx = x + directionsX[i];
            int ny = y + directionsY[i];

            if (nx >= 0 && nx < board.size() && ny >= 0 && ny < board.size()) {
                if (!visited[nx][ny] && board[nx][ny] == currentPlayer) {
                    visited[nx][ny] = true;
                    queue.emplace(nx, ny);
                }
            }
        }
    }
    return false;
}
