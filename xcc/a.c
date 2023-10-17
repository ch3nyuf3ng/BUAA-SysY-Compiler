int fib_inputs_array[2][2] = {{10, 2}, {3, 4}};

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

int deref_array_addr(int array_addr[][2])
{
    return array_addr[0][0];
}

int main()
{
    int input;
    input = deref_array_addr(fib_inputs_array);
    printf("fib(%d)=%d\n", input, fib(input));

    return 0;
}