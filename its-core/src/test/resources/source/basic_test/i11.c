#include<stdio.h>

int main()
{
    int a;
    scanf("%d",&a);
    printf("Reverse of %d is ",a); //slightly modified to remove minor typo in the word "Reverse"
    for(int i=a;i!=0;i=i/10)
    {
        int rem=i%10;
        printf("%d",rem);
    }

    return 0;
}