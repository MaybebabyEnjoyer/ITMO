import java.util.Scanner;

public class B {
    private static class Node {
        private final int value;
        private Node left;
        private Node right;
        private int height;

        public Node(final int value) {
            this.value = value;
            this.left = null;
            this.right = null;
            this.height = 0;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AVLTree tree = new AVLTree();
        while (sc.hasNext()) {
            final String operation = sc.next();
            final int value = sc.nextInt();
            switch (operation) {
                case "insert" -> {
                    if (!tree.exists(tree.root, value)) {
                        tree.root = tree.insert(tree.root, value);
                    }
                }
                case "delete" -> {
                    if (tree.exists(tree.root, value)) {
                        tree.root = tree.delete(tree.root, value);
                    }
                }
                case "exists" -> System.out.println(tree.exists(tree.root, value));
                case "next" -> System.out.println(tree.next(tree.root, value));
                case "prev" -> System.out.println(tree.prev(tree.root, value));
            }
        }
    }

    public static class AVLTree {
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
            recountHeight(current);
            recountHeight(temp);
            return temp;
        }

        private Node rotateRight(Node current) {
            Node temp = current.left;
            current.left = temp.right;
            temp.right = current;
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
                current.right = delete(current.right, value);
            } else {
                Node tempLeft = current.left;
                Node tempRight = current.right;
                if (tempRight == null) {
                    return tempLeft;
                }
                Node min = minimum(tempRight);
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

        // The next item in the search tree
        public String next(Node current, final int value) {
            String result = null;
            while (current != null) {
                if (value < current.value) {
                    result = String.valueOf(current.value);
                    current = current.left;
                } else {
                    current = current.right;
                }
            }
            if (result == null) {
                return "none";
            }
            return result;
        }

        // The previous item in the search tree
        public String prev(Node current, final int value) {
            String result = null;
            while (current != null) {
                if (value > current.value) {
                    result = String.valueOf(current.value);
                    current = current.right;
                } else {
                    current = current.left;
                }
            }
            if (result == null) {
                return "none";
            }
            return result;
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
