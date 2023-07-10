import java.io.*;
public class MergeSort {
    void merge(int[] arr, int l, int m, int r) {
        int it1 = 0;
        int it2 = 0;
        int[] res = new int[r - l];
        while ((l + it1 < m) && (m + it2 < r)) {
            if (arr[l + it1] < arr[m +it2]) {
                    res[it1 + it2] = arr[l + it1];
                    it1++;
            } else {
                res[it1 + it2] = arr[m + it2];
                it2++;
            }
        } while (l + it1 < m) {
            res[it1 + it2] = arr[l + it1];
            it1++;
        } while (m + it2 < r) {
            res[it1 + it2] = arr[m + it2];
            it2++;
        }
        if (it1 + it2 >= 0) System.arraycopy(res, 0, arr, l, it1 + it2);
    }
    void mergeSort(int[] arr, int l, int r) {
        if (l+1 >= r) {
            return;
        }
        int m = (l + r) / 2;
        mergeSort(arr, l, m);
        mergeSort(arr, m, r);
        merge(arr, l, m, r);
    }

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(in.readLine());
        int[] abc = new int[n];
        String[] nums = in.readLine().split(" ");
        int j = 0;
        for (String i : nums) {
            abc[j++] = Integer.parseInt(i);
        }
        MergeSort sort = new MergeSort();
        sort.mergeSort(abc, 0, abc.length);
        for (int k : abc) {
            System.out.print(k + " ");
        }
    }
}