#include<stdio.h>
//function to define prime numbers
int check_prime(int number) {
  int index=2;
  if (number==1) {
    return 0;
  }
  for (index=2;index<=number-1;index++) {
    if (number%index==0) {
    return 0;
    }
  }
  return 1;
}

int main() {
  int i,n1,n2;
  scanf("%d %d",&n1,&n2);
  for(i=n1;i<=n2;i++) {
    if (check_prime(i) == 1) {//Original code: if (check_prime(i))
      printf("%d ",i);
    }
  }
	return 0;
}