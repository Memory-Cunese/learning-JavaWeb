package cn.itcast.Test;

import java.util.Arrays;

//ѡ������
public class SelectSort {
   public static void main(String args[])
   {
	   int[] a={5,2,9,6,7,8,1,3,10,4};
	   for(int i=0;i<a.length;i++)
	   {
		   int min=i;
		   for(int j=i+1;j<a.length;j++)
		   {
			   if(a[min]>a[j])
			   {
				   //ʼ��ѡ����Сֵ������
				   min=j;
			   }
		   }
		   int temp=a[i];
		   a[i]=a[min];
		   a[min]=temp;
	   }
	   
	   System.out.println(Arrays.toString(a));
   }
}
