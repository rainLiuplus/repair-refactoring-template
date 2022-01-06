int main() {
    int a = 101;
    int result;
    switch (a % 3) {
        case 0: result = 0; break;
        case 1: result = 1; break;
        default: result = 2;
    }
}