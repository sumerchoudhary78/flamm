#include <iostream>
#include <vector>
#include <string>
using namespace std;

class Solution {
public:
    vector<vector<string>> solveNQueens(int n) {
        vector<vector<string>> result;
        vector<string> board(n, string(n, '.'));
        backtrack(board, 0, result);
        return result;
    }

private:
    void backtrack(vector<string>& board, int row, vector<vector<string>>& result) {
        if (row == board.size()) {
            result.push_back(board);
            return;
        }

        for (int col = 0; col < board.size(); col++) {
            if (isValid(board, row, col)) {
                board[row][col] = 'Q';
                backtrack(board, row + 1, result);
                board[row][col] = '.';
            }
        }
    }

    bool isValid(vector<string>& board, int row, int col) {
        int n = board.size();

        // Check column and both diagonals
        for (int i = 0; i < row; i++) {
            if (board[i][col] == 'Q' ||                           // same column
                (col - (row - i) >= 0 && board[i][col - (row - i)] == 'Q') ||  // main diagonal
                (col + (row - i) < n && board[i][col + (row - i)] == 'Q'))     // anti-diagonal
                return false;
        }
        return true;
    }
};

void printSolutions(const vector<vector<string>>& solutions) {
    cout << "Found " << solutions.size() << " solutions:\n";
    for (int i = 0; i < solutions.size(); i++) {
        cout << "\nSolution " << (i + 1) << ":\n";
        for (const string& row : solutions[i]) cout << row << "\n";
    }
    cout << "\n";
}

int main() {
    Solution sol;

    // Test cases from problem examples
    cout << "N-Queens for n=4:\n";
    printSolutions(sol.solveNQueens(4));

    cout << "N-Queens for n=1:\n";
    printSolutions(sol.solveNQueens(1));

    cout << "N-Queens for n=8: " << sol.solveNQueens(8).size() << " solutions\n";

    return 0;
}
