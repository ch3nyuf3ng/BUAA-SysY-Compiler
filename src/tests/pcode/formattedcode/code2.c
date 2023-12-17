int a_global;
const int b_global = 1;

void func1() {
    printf("21182621\n");
}

void func2(int a) {
    a = a - 1;
    return;
}

void func3(int a, int arr[], int arrrr[][2]) {
    a = arr[0] + arrrr[0][0];
    return;
}

int func4() {
    printf("21182621\n");
    return0;
}

int func5(int a) {
    a = a + 1;
    returna;
}

int func6(int a, int arr[]) {
    a = a + arr[0];
    returna;
}

int main() {
    int gett;
    gett = getint();
    const int a = 0;
    const int aa = 0, bb = 1;
    const int aaa = 0, bbb = 1, ccc = 2;
    const int arr_a[2] = {1, 2};
    const int arr_aa[2] = {1, 2}, arr_bb[3] = {3, 4, 5};
    const int arrrr_a[2][2] = {{1, 2}, {1, 2}};
    const int arrrr_aa[2][2] = {{1, 2}, {1, 2}}, arrrr_bb[1][3] = {{1, 2, 3}};
    printf("21182621\n");
    int d = 0;
    int dd = 0, ee = 1, ff = 2;
    int ddd, eee = 1;
    ddd = 0;
    int arr_d[2] = {1, 2};
    int arr_dd[2] = {1, 2}, arr_ee[3] = {3, 4, 5};
    int arr_ddd[2], arr_eee[3] = {3, 4, 5};
    arr_ddd[1] = 1;
    arr_eee[1] = 3;
    int arrrr_d[2][2] = {{1, 2}, {1, 2}};
    int arrrr_dd[2][2] = {{1, 2}, {1, 2}}, arrrr_ee[1][3] = {{1, 2, 3}};
    int arrrr_ddd[2][2] = {{1, 2}, {1, 2}}, arrrr_eee[1][3] = {{1, 2, 3}};
    arrrr_ddd[1][1] = 1;
    arrrr_eee[0][1] = 1;
    printf("21182621\n");
    func1();
    func2(1);
    func3(1, arrrr_d[0], arrrr_d);
    func4();
    func5(1);
    func6(1, arr_d);
    printf("21182621\n");
    int i = 5;
    int j;
    for (j = 1; j <= 5; j = j + 1) {
        ;
    }
    j = 1;
    for (; j <= 5; j = j + 1) {
        ;
        continue;
    }
    for (j = 1; ; j = j + 1) {
        break;
    }
    for (j = 1; j <= 5; ) {
        j = j + 1;
    }
    for (j = 1; ; ) {
        break;
    }
    for (; j <= 5; ) {
        break;
    }
    for (; ; j = j + 1) {
        break;
    }
    for (; ; ) {
        break;
    }
    {
        if (i == 3) {
            ;
        }  
        if (i == 2) {
            ;
        } else {
            ;
        }
    }
    printf("%d\n", a);
    printf("21182621\n");
    d = 1 + 2 * (-3) / 4 % 5;
    d = -+-d;
    if (3 != 4 || (!a)) {
        ;
    }  
    if (1 < 2 || 1 > 3 && 1 <= 2 || 1 >= 3) {
        ;
    }  
    {
        int d = 10;
    }
    {}
    printf("21182621\n");
    printf("21182621\n");
    printf("21182621\n");
    return0;
}