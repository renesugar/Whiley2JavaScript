function inc2(int x) -> (int r)
ensures r == x+2:
    int y = x+1
    assert y == x+1
    y = y+1
    assert y == x+2
    return y

public export method test():
    //
    assume inc2(0) == 2
    assume inc2(1) == 3
    assume inc2(2) == 4
