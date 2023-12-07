import java.util.Scanner;

public class F {
    private static class Node {
        private final int value;
        private Node left;
        private Node right;
        private int height;
        private int count;

        public Node(final int value) {
            this.value = value;
            this.left = null;
            this.right = null;
            this.height = 0;
            this.count = 0;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SearchTree tree = new SearchTree();
        int n = sc.nextInt();
        for (int i = 0; i < n; i++) {
            final int operation = sc.nextInt();
            final int value = sc.nextInt();
            switch (operation) {
                case -1 -> {
                    if (tree.exists(tree.root, value)) {
                        tree.root = tree.delete(tree.root, value);
                    }
                }
                case 0 -> System.out.println(tree.kth(tree.root, value));
                case 1 -> {
                    if (!tree.exists(tree.root, value)) {
                        tree.root = tree.insert(tree.root, value);
                    }
                }
            }
        }
    }

    public static class SearchTree {
        public Node root;

        // Add a new element to the search tree
        public Node insert(Node current, final int value) {
            if (root == null) {
                root = new Node(value);
                return root;
            }
            if (current == null) {
                return new Node(value);
            }
            if (value < current.value) {
                current.left = insert(current.left, value);
            } else if (value > current.value) {
                current.count++;
                current.right = insert(current.right, value);
            }
            return balance(current);
        }

        private Node balance(Node current) {
            recountHeight(current);
            if (differenceHeight(current) == 2) {
                if (differenceHeight(current.right) < 0) {
                    current.right = rotateRight(current.right);
                }
                return rotateLeft(current);
            }
            if (differenceHeight(current) == -2) {
                if (differenceHeight(current.left) > 0) {
                    current.left = rotateLeft(current.left);
                }
                return rotateRight(current);
            }
            return current;
        }

        private Node rotateLeft(Node current) {
            Node temp = current.right;
            current.right = temp.left;
            temp.left = current;
            current.count = current.count - (temp.count + 1);
            recountHeight(current);
            recountHeight(temp);
            return temp;
        }

        private Node rotateRight(Node current) {
            Node temp = current.left;
            current.left = temp.right;
            temp.right = current;
            temp.count = temp.count + (current.count + 1);
            recountHeight(current);
            recountHeight(temp);
            return temp;
        }

        private int countHeight(Node current) {
            return current != null ? current.height : 0;
        }

        private int differenceHeight(Node current) {
            return countHeight(current.right) - countHeight(current.left);
        }

        private void recountHeight(Node current) {
            current.height = Math.max(countHeight(current.left), countHeight(current.right)) + 1;
        }

        // Remove the element from the search tree
        public Node delete(Node current, final int value) {
            if (current == null) {
                return null;
            }
            if (value < current.value) {
                current.left = delete(current.left, value);
            } else if (value > current.value) {
                current.count--;
                current.right = delete(current.right, value);
            } else {
                Node tempLeft = current.left;
                Node tempRight = current.right;
                if (tempRight == null) {
                    return tempLeft;
                }
                Node min = minimum(tempRight);
                min.count = current.count - 1;
                min.right = removeMinimum(tempRight);
                min.left = tempLeft;
                return balance(min);
            }
            return balance(current);
        }

        private Node removeMinimum(Node current) {
            if (current.left == null) {
                return current.right;
            }
            current.left = removeMinimum(current.left);
            return balance(current);
        }

        private Node minimum(Node current) {
            if (current.left == null) {
                return current;
            }
            return minimum(current.left);
        }

        // Is the element present in the search tree?
        public boolean exists(Node current, final int value) {
            if (current == null) {
                return false;
            }
            if (value == current.value) {
                return true;
            } else if (value < current.value) {
                return exists(current.left, value);
            } else {
                return exists(current.right, value);
            }
        }

        public int kth(Node current, final int value) {
            if (current.count == value - 1) {
                return current.value;
            }
            if (current.count >= value) {
                return kth(current.right, value);
            } else {
                return kth(current.left, value - current.count - 1);
            }
        }

        public void toString(Node current) {
            if (current == null) {
                return;
            }

            toString(current.left);
            System.out.print(current.value + " ");
            toString(current.right);
        }
    }
}