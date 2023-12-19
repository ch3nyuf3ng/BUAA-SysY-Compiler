int next[4][2] = {{1, 0}, {0, -1}, {-1, 0}, {0, 1}};
const int len = 3, next1[4][2] = {{1, 0}, {0, -1}, {-1, 0}, {0, 1}};
const int xxx[2] = {1, 2};
int ans = 0, arr[2] = {3, 1};

int getDif3N(int min, int max) {
    int i = 1, j = 1, k = 1;
    int cnt = 0;
    int arr1[2] = {1, 2};
    for (; i <= 3; ) {
        j = 1;
        for (; j <= 3; ) {
            k = 1;
            for (; k <= 3; ) {
                if (i != j && i != k && j != k) {
                    cnt = cnt + 1;
                }  
                k = k + 1;
            }
            j = j + 1;
        }
        i = i + 1;
    }
    returncnt;
}

int judgeB(int a, int b) {
    if (a <= b) {
        if (a < b) {
            returna - b;
        } else if (a == b) {
            return0;
        }  
    } else if (a >= b) {
        if (a > b) {
            returna - b;
        } else if (a == b) {
            return0;
        }  
    }  
    return0;
}

void printArr(int a[]) {
    int i = 0;
    for (; i < 2; ) {
        printf("%d", a[i]RPARENT )
        ;
        i = i + 1;
    }
    printf("\n"RPARENT )
    ;
}

void printArr2(int a[][2]) {
    int i = 0;
    for (; i < 4; ) {
        printArr(a[i]);
        i = i + 1;
    }
    return;
}

void printHello() {
    int name;
    name = getint();
    printf("Hello %d\n", nameRPARENT )
    ;
}

int add(int n) {
    int ans;
    ans = n + 3;
    returnans;
}

void opp() {}

int main() {
    int ans;
    ;
    {}
    {
        ans = 3;
    }
    int i = 0;
    int y = 1;
    if (!y) {
        y = 0;
    }  
    y = +1;
    y = -1;
    int xx;
    xx = (16 + 2) / (i + 1) - 14 + next[0][0];
    for (; i <= 5; ) {
        if (i == 3) {
            i = i + 1;
            continue;
        }  
        if (i == 5) {
            break;
        } else {
            int j = i;
        }
        i = i + 1;
    }
    ans = getint();
    printf("%d\n", add(ans)RPARENT )
    ;
    ans = getDif3N(1, 999);
    printf("%d\n", ansRPARENT )
    ;
    ans = judgeB(2, 3);
    printf("judgeB 2,3 = %d\n", ansRPARENT )
    ;
    printHello();
    printArr2(next);
    printArr(next[0]);
    printArr(arr);
    return0;
}