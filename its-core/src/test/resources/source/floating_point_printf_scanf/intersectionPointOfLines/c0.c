#include<stdio.h>

int main(){
	float a1,b1, a2,b2;
	float d,a,b;

	scanf("%f %f %f %f",&a1,&b1,&a2,&b2);

	d = a1*b2 - a2*b1;

	if (d==0)
	    printf("INF\n");
	else {
	    a = (b2-b1)/d;
	    b = (a1-a2)/d;
	    printf("(%.3f,%.3f)\n", a,b);

	}

	return 0;
}