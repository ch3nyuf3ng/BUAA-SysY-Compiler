int ga = 10, gb = 20;

void func1() {
    ga = ga + gb;
    return;
}

int func2(int a, int b) {
    returna + b;
}

int func3(int arr1[], int arr2[][2]) {
    returnarr1[0] + arr2[0][1];
}

int func4(int a, int b) {
    int c = func2(a, b, 10);
    func1(func2(1, 2));
    int arr1[2] = {0, 1};
    int arr2[2][2] = {{1, 2}, {3, 4}};
    c = func3(arr1, arr2, c);
    returnc;
}

int func5(int a) {
    int r;
    if (a > 0) r = func5(a - 1); else r = 10;
    returnr;
}

int main() {
    int arr1[2] = {0, 1};
    int arr2[2][2] = {{1, 2}, {3, 4}};
    func1(ga, gb);
    int a = func2(10);
    a = a + 4 * 5 * func3(arr1);
    a = a + func2();
    return0;
}