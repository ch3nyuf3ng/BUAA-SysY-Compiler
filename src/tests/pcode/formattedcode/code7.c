
int main() {
    int a = 5;
    int b = 6;
    int s1[6] = {0, 1, 2, 3, 6, 12};
    int s2[2][3] = {{10, 11, 12}, {13, 14, 15}};
    if (!s1[0] == 1 && !(s1[0] * 2) && !0) {
        printf("! is ok\n");
    } else {
        printf("! has problem\n");
    }
    if (a + b < s1[5] && s1[3] - s1[0] < s2[1][2] / a == 0 && b < a + b < s1[0] == 0 && 0 == b < a + b < s1[2] > s1[1]) {
        printf("< is ok\n");
    } else {
        printf("< has problem\n");
    }
    if (a * b > s1[2] * s2[1][2] == 0 && (s2[0][1] - s1[2]) % a > s2[1][2] / a && s1[5] > a + b + 2 > 0 == 0 && s1[5] > a + b < s1[1] == 0) {
        printf("> is ok\n");
    } else {
        printf("> has problem\n");
    }
    if (a <= b && a <= b + s1[1] && a <= b <= s1[0] == 0 && a <= b >= s1[2] < 1) {
        printf("<= is ok\n");
    } else {
        printf("<= has problem\n");
    }
    if (b >= a + 1 && b >= s1[4] + s1[1] != 1 && a <= b >= s1[1] + 1 == 0 && a >= b >= s1[0] <= s1[0] != 1) {
        printf(">= is ok\n");
    } else {
        printf(">= has problem\n");
    }
    if (a != b && a == b == 0 && a > b == a < b != 1 && a >= b == s1[2] != s1[0] != s1[4]) {
        printf("== != is ok\n");
    } else {
        printf("== != has problem\n");
    }
    if (s1[3] == s2[0][1] || a >= b || a + 7 % 4 < b * 3) {
        printf("or is ok\n");
    } else {
        printf("or has problem\n");
    }
    if (a / 2 < b - 3 || s1[0] + 1 > s1[5] && b > s1[5]) {
        printf("the priority of and/or is ok\n");
    } else {
        printf("the priority of and/or has problem\n");
    }
    if (a < b || +a) {
        a = a + 1;
        if (s1[0] < s1[1] && +b) {
            b = b + 1;
            printf("%d %d\n", a, b);
        }  
    }  
    return0;
}