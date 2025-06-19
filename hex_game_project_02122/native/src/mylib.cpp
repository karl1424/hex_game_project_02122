#include <jni.h>
#include "dk_dtu_main_NativeWrapper.h"
#include "MCTS.h"
#include <vector>
#include <jni.h>

std::vector<std::vector<int>> parseBoard(JNIEnv *env, jobjectArray board2D)
{
    jsize numRows = env->GetArrayLength(board2D);
    std::vector<std::vector<int>> board;

    for (jsize i = 0; i < numRows; ++i)
    {
        jintArray row = (jintArray)env->GetObjectArrayElement(board2D, i);
        jsize numCols = env->GetArrayLength(row);

        jint *rowElements = env->GetIntArrayElements(row, 0);
        std::vector<int> cppRow(rowElements, rowElements + numCols);

        env->ReleaseIntArrayElements(row, rowElements, 0);
        env->DeleteLocalRef(row);

        board.push_back(cppRow);
    }

    return board;
}

JNIEXPORT jintArray JNICALL Java_dk_dtu_main_NativeWrapper_runAlgorithmNative(JNIEnv *env, jobject obj, jobjectArray board2D, jint playerNumber, jint iterations)
{
    std::vector<std::vector<int>> board = parseBoard(env, board2D);

    MCTS mcts(board, playerNumber, iterations);
    auto [x, y] = mcts.runAlgorithm();

    jintArray result = env->NewIntArray(2);
    jint temp[2] = {x, y};
    env->SetIntArrayRegion(result, 0, 2, temp);
    return result;
}
