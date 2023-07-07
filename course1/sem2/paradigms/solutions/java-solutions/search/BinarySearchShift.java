package search;
//in this solution we're assuming that i is a number from N, that is not bigger than the length of the array
//Saying more formally i in [0, args.length) and i -> int

//P: forall i in [0, args.length - 1): args[i] -> int && args is cycle shift of array such that forall i in [0, args.length): args[i] > args[i+1]
//Q: args.length == 0 && R == 0 || args.length > 0 && R = i: args[i] == max(args)
public class BinarySearchShift {
    public static void main(String[] args) {
        int a = 0;
        // args.length == 0 && R == 0 || args.length > 0 && R = args[args.length - 1]
        if (args.length > 0) {
            a = Integer.parseInt(args[args.length - 1]);
        }
        System.out.println(iterativeBinarySearch(args, -1, args.length, a));
    }

    //P: forall i in [0, args.length): args[i] -> int &&
    // args is cycle shift of array such that forall i in [0, args.length): args[i] > args[i+1] &&
    // l in [-1, args.length) && r in [0, args.length] && (arr.length == 0 && a == 0 || arr.length > 0 && a == arr[arr.length - 1])
    // r <= l
    //Q: arr.length > 1 && R = i: args[i] == max(args) || arr.length == 1 && R = 0

    static int iterativeBinarySearch(String[] arr, int l, int r, int a) {
        //define mid as the middle of the array, more formally as (l + r) / 2

        if (arr.length <= 1) {
            return 0;
            // arr.length == 1 && R = 0
        }
        //Inv: (l == 0 || (l > 0) && (arr[l] > a)) && (r == arr.length || (r < arr.length) || (r < arr.length) && (arr[r] > a)) && r > l + 1
        //&& arr.length > 1
        while (r - l > 1) {
            //Inv && (2 * mid - l - r == 0) || (2 * mid + 1 == l + r)
            int m = l + (r - l) / 2;
            //Inv && m == mid
            if (Integer.parseInt(arr[m]) > a) {
                //Inv && m == mid && arr[m] > a
                r = m;
                //Inv && m == mid && arr[m] > a && l == mid
            } else {
                //Inv && m == mid && arr[m] < a
                l = m;
                //Inv && m == mid && arr[m] < a && r == mid
            }
            //Inv
        }
        //arr.length > 1 && (l == 0 || (l > 0) && (arr[l] < a)) && (r == arr.length || (r < arr.length) || (r < arr.length) && (arr[r] > a)) && r - l == 1
        // as on each we're narrowing down the interval by half (we can say for each step arr.length / 2 = new arr.length)
        // and minimal interval is 1, we can say that given algorithm will work correctly and will return the answer

        if (r == arr.length) {
            return r - 1;
            //R = r - 1 && r == arr.length
        }
        return r;
        //R = r && r != arr.length
    }

    //P: forall i in [0, args.length): args[i] -> int &&
    // args is cycle shift of array such that forall i in [0, args.length): args[i] > args[i+1] &&
    // l in [-1, args.length) && r in [0, args.length] && (arr.length == 0 && a == 0 || arr.length > 0 && a == arr[arr.length - 1])
    // && l<=r
    //Q: arr.length > 1 && R = i: args[i] == max(args) || arr.length == 1 && R = 0
    private static int recursiveBinarySearch(String[] arr, int l, int r, int last) {
        if (arr.length <= 1) {
            return 0;
            // arr.length == 1 && R = 0
        }

        if (r - l <= 1) {
            if (r == arr.length) {
                // r <= l + 1 && Inv && r == arr.length
                return r - 1;
            }
            // r <= l + 1 && Inv && r != arr.length
            return r;
        }
        // Inv && (2 * mid - l - r == 0) || (2 * mid + 1 == l + r)
        int m = l + (r - l) / 2;
        // Inv && m == mid
        if (Integer.parseInt(arr[m]) < last) {
            // Inv && arr[m] < a
            return recursiveBinarySearch(arr, l, m, last);
        } else {
            // Inv && arr[m] > a
            return recursiveBinarySearch(arr, m, r, last);
        }
    }
}