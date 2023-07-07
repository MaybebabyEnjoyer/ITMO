package search;
//in this solution we're assuming that i is a number from N, that is not bigger than the length of the array
//Saying more formally i in [0, args.length) and i -> int

import java.util.Arrays;

public class BinarySearch {

    // :NOTE: if args.length == 1 => IndexOutOfRange
    //P1: args.length > 0 && (forall i in [0, args.length - 1): args[i] -> int && args[i+1] <= args[i])
    //Q: R = min(i) - 1 where i in [0, args.length) that args[i] <= args[0] or args.length - 1 if no such i

    public static void main(String[] args) {
        //P2: P1 && args[0] = x
        int n = Integer.parseInt(args[0]);
        //--------CONTRACT FOR STREAM AND ODD/EVEN CHECKER FUNCTION--------
        //P: args.length > 0 && (forall i in [0, args.length): args[i] -> int && forall i in [0, args.length): (args[0]+args[1]+...+args[i]) -> int
        //Q: iterativeBinarySearch if forall i in [0, args.length): (args[0] + args[1]+ ... + args[i]) is even, recursiveBinarySearch otherwise
        //It works as '1' in binary representation is 00...1 and we're checking if the last bit is 1.
        //--------CONTRACT FOR BISECTION FUNCTION--------
        //P3: P1 && n = x

        System.out.println((Arrays.stream(args).mapToInt(Integer::parseInt).sum() & 1) == 0 ?
                recursiveBinarySearch(args, n, 0, args.length) :
                iterativeBinarySearch(args, n, 0, args.length));
        //        System.out.println(iterativeBinarySearch(args, n, 0, args.length));
        //System.out.println(recursiveBinarySearch(args, n, 0, args.length));
    }

    //P: arr.length > 0 && (forall i in [0, arr.length) : arr[i] -> int && arr[i+1] <= arr[i]) && l in [0, arr.length) && r in [0, arr.length) && l <= r && args[0] = x
    //Q: R = min i: arr[i] <= x || arr.length - 1 if no such i
    static int iterativeBinarySearch(String[] arr, int x, int l, int r) {
        //define mid as the middle of the array, more formally as (l + r) / 2
        // Inv: (l == 0 || (l > 0 && arr[l-1] > x)) && (r == arr.length || (r < arr.length && arr[r] <= x)) && l <= r
        while (r - l > 1) {
            // Inv && (2 * mid - l - r == 0) || (2 * mid + 1 == l + r)
            int m = l + (r - l) / 2;
            // Inv && m == mid
            if (Integer.parseInt(arr[m]) > x) {
                // Inv && m == mid && arr[m] > x
                l = m;
                // Inv && m == mid && arr[m] > x && l == mid
            } else {
                // Inv && m == mid && arr[m] <= x
                r = m;
                // Inv && m == mid && arr[m] <= x && r == mid
            }
            // Inv
        }
        // arr.length > 0 && (l == 0 || (l > 0 && arr[l-1] > x)) && (r == arr.length || (r < arr.length && arr[r] <= x)) && (r - l == 1)
        // as on each we're narrowing down the interval by half (we can say for each step arr.length / 2 = new arr.length)
        // and minimal interval is 1, we can say that given algorithm will work correctly and will return the answer.
        return r - 1;
    }

    // :NOTE: l == 1000 && r == 1
    //P: arr.length > 0 && forall i in [0, arr.length): arr[i] -> int && arr[i+1] <= arr[i] && l in [0, arr.length) && r in [0, arr.length] =: Pre && l <= r
    // Q: R = min i: arr[i] <= x || arr.length - 1 if no such i
    static int recursiveBinarySearch(String[] arr, int x, int l, int r) {
        if (r - l <= 1) {
            //r - l <= 1 && Inv
            return r - 1;
        }
        //(2 * mid - l - r == 0) || (2 * mid + 1 == l + r)
        int m = l + (r - l) / 2;
        // m == mid
        if (Integer.parseInt(arr[m]) > x) {
            // Inv && arr[m] > x
            return recursiveBinarySearch(arr, x, m, r);
        } else {
            // Inv && arr[m] <= x
            return recursiveBinarySearch(arr, x, l, m);
        }
    }
}