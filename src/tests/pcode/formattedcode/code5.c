const int c_common = 10, c_array[3] = {1, 2, 3};
const int c_matrix[3][3] = {{1, 2, 3}, {1, 2, 3}, {1, 2, 3}};
int v_common, v_array[3], v_matrix[3][3];
int v_commonInit = 10;
int v_arrayInit[3] = {1, 2, 3};
int v_matrixInit[3][3] = {{1, 2, 3}, {1, 2, 3}, {1, 2, 3}};

void print(int output) {
    printf("\nf2() flag : %d", outputRPARENT )
    ;
    return;
}

int f0() {
    return1;
}

int f1(int a[], int b[][3], int c[]) {
    returna[0] + b[0][0] + c[0];
}

int f2(int a, int b) {
    int c;
    int flag;
    int a1[2] = {1, 2};
    int a2[3][3] = {{1, 2, 3}, {1, 2, 3}, {1, 2, 3}};
    c = -(a * b) + a / b + (a % b) - 1 + f1(a1, a2, a2[0]) - +f0();
    if (a > 10 && b > 10) {
        print(1);
        flag = 0;
    }  
    if (a > 10 && b <= 10) {
        print(2);
        flag = 0;
    }  
    if (a <= 10 && b <= 10) {
        print(3);
        flag = 1;
    }  
    if (a <= 10 && b > 10) {
        print(4);
        flag = 1;
    }  
    returnflag;
}

int main() {
    ;
    {}
    int a, b, flag1, flag2, flag, i;
    a = getint();
    b = getint();
    i = 0;
    if (a != b) {
        for (; i < 3; ) {
            int c1;
            int c2;
            c1 = a + b;
            c2 = a - b;
            flag1 = f2(c1, c2);
            flag2 = f2(c2, c1);
            if (flag1 == 0 || !flag2) {
                flag = 0;
            } else {
                flag = 1;
            }
            printf("\nflag = %d : c1 = %d, c2 = %d", flag, c1, c2RPARENT )
            ;
            i = i + 1;
            a = a + 5;
            b = b + 5;
            if (i >= 10) {
                continue;
            }  
            if (i < 0) {
                break;
            }  
        }
    }  
    return0;
}