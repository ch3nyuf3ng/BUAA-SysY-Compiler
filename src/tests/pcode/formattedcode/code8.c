const int a1 = 1, a2 = +3, a3 = 8;
int b1 = 0 + 2, b2 = -5, b3 = +6;

int main() {
    printf("\n"RPARENT )
    ;
    int n = 10;
    for (; n; ) {
        n = n - 1;
        if (n < b3) {
            continue;
            printf("Continue is error!\n"RPARENT )
            ;
        }  
        if (n < a1) {
            break;
            printf("Break is error!And < is error!\n"RPARENT )
            ;
        }  
        if (n == a2) {
            printf("+ is correct!\n"RPARENT )
            ;
        } else {
            printf("+ is error!\n"RPARENT )
            ;
        }
        if (n == b1) {
            break;
        }  
    }
    if (n != b1) {
        if (n == 0) {
            printf("Break is error!\n"RPARENT )
            ;
        } else {
            printf("Continue is error!\n"RPARENT )
            ;
        }
    }  
    printf("a1+b1 is %d\n", a1 + b1RPARENT )
    ;
    printf("a2+b2 is %d\n", a2 + b2RPARENT )
    ;
    return0;
}