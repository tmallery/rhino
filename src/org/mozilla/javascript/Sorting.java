package org.mozilla.javascript;

import java.util.Arrays;
import java.util.Comparator;

public final class Sorting {
    private static final int SMALLSORT = 16;

    public static void insertionSort(Object[] a, Comparator<Object> cmp)
    {
        insertionSort(a, 0, a.length - 1, cmp);
    }

    public static void insertionSort1(Object[] a, int start, int end, Comparator<Object> cmp)
    {
        int i = start;
        while (i <= end) {
            int j = i;
            while ((j > start) && (cmp.compare(a[j-1], a[j]) > 0)) {
                swap(a, j, j-1);
                j--;
            }
            i++;
        }
    }

    public static void insertionSort(Object[] a, int start, int end, Comparator<Object> cmp)
    {
        int i = start;
        while (i <= end) {
            Object x = a[i];
            int j = i - 1;
            while ((j >= start) && (cmp.compare(a[j], x) > 0)) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = x;
            i++;
        }
    }

    public static void heapsort(Object[] a, Comparator<Object> cmp)
    {
        heapsort(a, 0, a.length - 1, cmp);
    }

    public static void heapsort(Object[] a, int start, int end, Comparator<Object> cmp)
    {
        heapify(a, start, end, cmp);

        int e = end;
        while (e > 0) {
            swap(a, e, 0);
            e--;
            siftDown(a, 0, e, cmp);
        }
    }

    public static void quicksort(Object[] a, Comparator<Object> cmp)
    {
        quicksort(a, 0, a.length - 1, cmp);
    }

    public static void quicksort(Object[] a, int start, int end, Comparator<Object> cmp)
    {
        if (start < end) {
            int p = partition(a, start, end, cmp);
            quicksort(a, start, p, cmp);
            quicksort(a, p + 1, end, cmp);
        }
    }

    /*
    Hybrid sorting mechanism similar to Introsort by David Musser. Uses quicksort's
    partitioning mechanism recursively until the resulting array is small or the
    recursion is too deep, and then use insertion sort for the rest.
    This is the same basic algorithm used by the GNU Standard C++ library.
    */
    public static void hybridSort(Object[] a, Comparator<Object> cmp)
    {
        hybridSort(a, 0, a.length - 1, cmp, log2(a.length) * 2);
    }

    private static void hybridSort(Object[] a, int start, int end, Comparator<Object> cmp, int maxdepth)
    {
        if (start < end) {
            if ((maxdepth == 0) || ((end - start) <= SMALLSORT)) {
                insertionSort(a, start, end, cmp);
            } else {
                int p = partition(a, start, end, cmp);
                hybridSort(a, start, p, cmp, maxdepth - 1);
                hybridSort(a, p + 1, end, cmp, maxdepth - 1);
            }
        }
    }

    /*
    Quicksort-style partitioning, using the Hoare partition scheme described on Wikipedia.
    Use the "median of three" method to determine which index to pivot on, and then
    separate the array into two halves based on the pivot.
    */
    private static int partition(Object[] a, int start, int end, Comparator<Object> cmp) {
        Object pivot = a[median(start, end, start + ((end - start) / 2))];
        int i = start - 1;
        int j = end + 1;
        while (true) {
            do {
                i++;
            } while (cmp.compare(a[i], pivot) < 0);
            do {
                j--;
            } while (cmp.compare(a[j], pivot) > 0);
            if (i >= j) {
                return j;
            }
            swap(a, i, j);
        }
    }

    private static void heapify(Object[] a, int start, int end, Comparator<Object> cmp)
    {
        int i = iParent(end);
        while (i >= 0) {
            siftDown(a, i, end, cmp);
            i--;
        }
    }

    private static void siftDown(Object[] a, int start, int end, Comparator<Object> cmp)
    {
        int root = start;

        while (iLeftChild(root) <= end) {
            int child = iLeftChild(root);
            int swap = root;

            if (cmp.compare(a[swap], a[child]) < 0) {
                swap = child;
            }
            if (((child + 1) <= end) && (cmp.compare(a[swap], a[child + 1]) < 0)) {
                swap = child + 1;
            }
            if (swap == root) {
                return;
            }
            swap(a, root, swap);
            root = swap;
        }
    }

    private static void swap(Object[] a, int l, int h)
    {
        Object tmp = a[l];
        a[l] = a[h];
        a[h] = tmp;
    }

    private static int iParent(int i)
    {
        return (int)Math.floor((i - 1) / 2.0);
    }

    private static int iLeftChild(int i)
    {
        return (i * 2) + 1;
    }

    private static int iRightChild(int i)
    {
        return (i * 2) + 2;
    }

    private static int log2(int n)
    {
        return (int)(Math.log10(n) / Math.log10(2.0));
    }

    private static int median(int n1, int n2, int n3)
    {
        int[] a = {n1, n2, n3};
        Arrays.sort(a);
        return a[1];
    }

    private static String fmtArray(Object[] a, int start, int end)
    {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = start; i <= end; i++) {
            sb.append(' ').append(a[i]);
        }
        sb.append(']');
        return sb.toString();
    }
}
