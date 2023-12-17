const int a = 1, b = 2;
const int c = 3;
int d, e = 1 + 2;
int f;

int double2(int x1) {
    if (!x1) {
        return2 * x1;
    } else {
        return(2 * -x1);
    }
    return0;
}

int add(int x1, int x2) {
    int sum = 0;
    sum = x1 + x2;
    returnsum;
}

int sub(int x1, int x2) {
    int sum = 0;
    sum = x1 - x2;
    if (sum >= 0) {
        returnsum;
    }  
    if (sum <= 0) {
        return0;
    }  
    return0;
}

void print_none() {
    return;
}

int main() {
    {
        int x = 0;
        int flag;
        int temp;
        int x1, x2;
        for (; x < 4; ) {
            if (x != 4) {
                x = x + 1;
            } else {
                break;
            }
            flag = getint();
            if (flag == 0) {
                x1 = getint();
                temp = double2(x1) % 1;
                printf("%d ", temp);
                temp = double2(x1) / +1;
                printf("%d\n", temp);
                continue;
            }  
            if (flag > 0) {
                x1 = getint();
                x2 = getint();
                printf("%d\n", add(x1, x2));
                continue;
            }  
            if (flag < 0) {
                x1 = getint();
                x2 = getint();
                printf("%d\n", sub(x1, x2));
                continue;
            }  
        }
    }
    return0;
}