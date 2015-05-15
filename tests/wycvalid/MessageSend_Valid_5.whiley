

type Sum is &{int result, [int] items}

method start(Sum this) -> void:
    int sum = 0
    for i in this->items:
        sum = sum + i
    this->result = sum

method get(Sum this) -> int:
    return this->result

method create([int] items) -> Sum:
    return new {result: 0, items: items}

public export method test() -> void:
    [int] data = [1, 3, 5, 7, 3, 198, 1, 4, 6]
    Sum sum = create(data)
    start(sum)
    assume get(sum) == 228