int fib_inputs_array[2][] = {{1, 2}, {3, 4}};

int fib(int x)
{
    if (x <= 1)
    {
        return 1;
    }
    if (x == 2)
    {
        return 2;
    }
    return fib(x - 1) + fib(x - 2);
}

int deref_array_addr(int array_addr[])
{
    return array_addr[0];
}

int main()
{
    for (int i = 0; i < 2; i = i + 1)
    {
        for (int j = 0; j < 2; j = j + 1)
        {
            int input = deref_array_addr(fib_inputs_array[i][j]);
            printf("fib(%d)=%d\n", input, fib(input));
        }
    }
    return 0;
}