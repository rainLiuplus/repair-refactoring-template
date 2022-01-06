#include <stdio.h>
int main() {

	int N;
	int a, b, c , count = 0;

	scanf("%d",&N);

	for(a = 1 ; a <= N ; a++)
		for(b = 1 ; b <= a ; b++)
			for(c =1 ; c <= b ; c++)
				if ( a + b > c && b + c > a && c + a > b)
					count++;


	printf("Number of possible triangles is %d" , count);

	return 0;
}