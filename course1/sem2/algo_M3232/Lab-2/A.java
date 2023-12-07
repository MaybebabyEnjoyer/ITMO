import java.util.Scanner;

public class A {
    private static class Node {
        private int value;
        private Node left;
        private Node right;

        public Node(final int value) {
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SearchTree tree = new SearchTree();
        while (sc.hasNext()) {
            final String operation = sc.next();
            final int value = sc.nextInt();
            switch (operation) {
                case "insert" -> {
                    if (!tree.exists(tree.root, value)) {
                        tree.insert(tree.root, value);
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

    public static class SearchTree {
        public Node root;

        // Add a new element to the search tree
        public void insert(Node current, final int value) {
            if (root == null) {
                root = new Node(value);
                return;
            }
            if (value < current.value) {
                if (current.left == null) {
                    current.left = new Node(value);
                } else {
                    insert(current.left, value);
                }
            } else if (value > current.value) {
                if (current.right == null) {
                    current.right = new Node(value);
                } else {
                    insert(current.right, value);
                }
            }
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
            } else if (current.left != null && current.right != null) {
                current.value = minimum(current.right).value;
                current.right = delete(current.right, value);
            } else {
                if (current.left != null) {
                    current = current.left;
                } else if (current.right != null) {
                    current = current.right;
                } else {
                    current = null;
                }
            }
            return current;
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
