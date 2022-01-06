#include<stdio.h>

int main()
{
    int n,rev = 0,m;
    scanf("%d",&m);
    n = m;
    while(n>0) {
        rev = (rev*10);
        rev = rev + n%10;
        n = n/10;

    }
    printf("Reverse of %d is %d",m,rev);
    return 0;
}