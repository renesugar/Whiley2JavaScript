// Test final local variable
function id(int x) -> (int r)
ensures r == x:
    //
    final int y = x
    return y

public export method test():
    assert id(-1) == -1
    assert id(0) == 0
    assert id(1) == 1