import java.nio.ByteBuffer;
import java.util.*;

public class HuffmanBytes {
    public static void main(String[] args) {
        String s = "abeeeeccccddddd";
        byte[] b = s.getBytes();
        HuffmanBytes h = new HuffmanBytes(b);
        System.out.println("header " + h.header.toString());
        System.out.println("CONTENT  " + h.getEncodedContent());
        System.out.println("EVERYTHING   " + h.encodedFile);
    }

    //TODO remove redundant variables and make the function return it as a result instead of global variables
    private Queue<NodeByte> minHeap;
    private NodeByte huffmanTree; //used in decoding and encoding
    private byte[] inputAsBytes;
    private String encodedString;
    private List<Byte> encodedContent; //without header
    private List<Byte> encodedFile; //with header
    private HashMap<Byte, Integer> occurrence;
    private HashMap<Byte, String> encodingTable;
    private List<Byte> header;
    private String decodedString;

    private List<Byte> decodedFile;

    //TODO remove the below parameters
    private List<Byte> charactersSize;
    private List<Byte> charactersInPreOrder;
    private List<Byte> treeSizeInBytes;
    private List<Byte> treeStructureBytes;

    public HuffmanBytes(byte[] inputAsBytes) {
        if (inputAsBytes.length == 0) {
            encodedString = "";
            encodedContent = new ArrayList<>();
            return;
        }
        this.encodedFile = new ArrayList<>();
        this.minHeap = new PriorityQueue<>();
        this.occurrence = new HashMap<>();
        this.encodingTable = new HashMap<>();
        this.inputAsBytes = inputAsBytes;
        this.header = new ArrayList<>();
        this.treeStructureBytes = new ArrayList<>();
        this.treeSizeInBytes = new ArrayList<>();
        this.charactersSize = new ArrayList<>();
        this.charactersInPreOrder = new ArrayList<>();

        //sort according to freq
        calculateFrequency();
        sortOccurrence();
        encode();

        //input from file is array of bytes, while encoding outputs a string
        //so we convert string into bytes
        encodedContent = convertStringIntoBytes(encodedString);

        //convert encodedBytes to list
        //create new list, add tree first, then encodedBytes
        createHeader(this.huffmanTree);
        createEncodedFile();

        //decode();
    }

    // a constructor should have a way of determining whether
    // decoding or encoding is needed, so mode parameter is added
    // c means compressing (encoding), d means decompressing
    public HuffmanBytes(byte[] inputAsBytes, char mode) {
        this.encodedFile = new ArrayList<>();
        this.minHeap = new PriorityQueue<>();
        this.occurrence = new HashMap<>();
        this.encodingTable = new HashMap<>();
        this.inputAsBytes = inputAsBytes;
        this.header = new ArrayList<>();
        this.treeStructureBytes = new ArrayList<>();
        this.treeSizeInBytes = new ArrayList<>();
        this.charactersSize = new ArrayList<>();
        this.charactersInPreOrder = new ArrayList<>();
        encodedString = "";
        encodedContent = new ArrayList<>();
        encodedFile = new ArrayList<>();
        decodedFile = new ArrayList<>();

        if (inputAsBytes.length == 0) return;

        if (mode == 'c') {
            //encode
            //if it's an empty file

            startEncode();
        } else if (mode == 'd') {
            //decode
            startDecode();
        }
    }

    private void startEncode() {
        //sort according to freq
        calculateFrequency();
        sortOccurrence();
        encode();
        //input from file is array of bytes, while encoding outputs a string
        //so we convert string into bytes
        encodedContent = convertStringIntoBytes(encodedString);
        //createHeader(this.huffmanTree);
        createHeader();
        createEncodedFile();
    }

    private void startDecode() {
        //we have input as bytes
        //read header
        //convert byte header to string header
        int index = 0; // iterating manually on input bytes
        //first 4 bytes are number of characters
        byte[] numOfCharacters = Arrays.copyOfRange(inputAsBytes, index, index + 4);
        int numOfCharactersInt = byteToInt(numOfCharacters);
        index = index + 4; //now index points to first character
        //read characters, each character is stored in its ASCII representation
        byte[] charactersAsBytes = Arrays.copyOfRange(inputAsBytes, index, index + numOfCharactersInt);

        index = index + numOfCharactersInt; //now index points to tree structure size
        List<String> charactersInPreOrder = new ArrayList<>();
        //convert bytes of characters to String and store in a list
        //TODO can convert characters to list while reading from file
        //TODO we can leave this as bytes without converting to String
        for(byte b : charactersAsBytes) {
            String s = "";
            s = s.concat(Character.toString((char)b));
            charactersInPreOrder.add(s);
        }
        byte[] treeStructureSize = Arrays.copyOfRange(inputAsBytes, index, index + 4);
        int treeStructureSizeInt = byteToInt(treeStructureSize);
        index = index + 4; //now index points to start of tree structure
        byte[] treeStructureInBytes =  Arrays.copyOfRange(inputAsBytes, index, index + treeStructureSizeInt);
        index = index + treeStructureSizeInt; //now index point to start of encoded content
        //convert treeStructureInBytes to String
        //TODO test -128 on below method
        String treeStructureString = ParseString.bitsArrayToString(treeStructureInBytes);
        //create pre-order string
        //TODO we can leave characters and structure as seperate entites and modify the createTree method
        String preorder = "";
        int characteri = 0;
        for(int itera = 0; itera < treeStructureString.length(); itera++) {
            preorder = preorder.concat(String.valueOf(treeStructureString.charAt(itera)));
            if(treeStructureString.charAt(itera) == '1') {
                preorder = preorder.concat(String.valueOf(charactersInPreOrder.get(characteri)));
                characteri = characteri + 1;
            }
        }

        //create tree
        NodeByte huffmanTree = createTree(charactersAsBytes, treeStructureString);

        //read content
        byte[] encodedContentAsBytes = Arrays.copyOfRange(inputAsBytes, index, inputAsBytes.length);
        String encodedContentAsString = ParseString.bitsArrayToString(encodedContentAsBytes);

        //decode content
        String decodedString = decode(encodedContentAsString, huffmanTree);
        this.decodedString = decodedString;
    }

    private String decode(String encodedContentAsString, NodeByte huffmanTree) {
        NodeByte temp = huffmanTree; //start iterating from the root
        String decodedString = "";
        for (int i = 0; i < encodedContentAsString.length(); i++) {
            char c = encodedContentAsString.charAt(i);

            if(temp.b != null) {
                decodedString = decodedString.concat(String.valueOf(temp.b));
                temp = huffmanTree;
            }

            //if current character is 0, go left in the tree (by convention)
            if(c == '0') {
                if(temp.left != null) {
                    temp = temp.left;
                    if(temp.b != null) {
                        decodedString = decodedString.concat(String.valueOf(temp.b));
                        temp = huffmanTree;
                    }
                }
            } else { // if current character is 1
                if(temp.right != null) {
                    temp = temp.right;
                    if(temp.b != null) {
                        decodedString = decodedString.concat(String.valueOf(temp.b));
                        temp = huffmanTree;
                    }
                }
            }

        }

        return decodedString;
    }
/*
    private Node getHuffmanTreeFromStringHeader(String header) {
        int index = 0;
        //read header
        int numOfCharacters = Character.getNumericValue(header.charAt(index));
        index = index + 1;
        Character[] characters = new Character[numOfCharacters];
        for(int i = 0; i < numOfCharacters; i++) {
            characters[i] = header.charAt(index);
            index = index + 1;
        }
        int treeStructureSize = Character.getNumericValue(header.charAt(index));
        index = index + 1;
        Character[] treeStructure = new Character[treeStructureSize];
        for(int i = 0; i < treeStructureSize; i++) {
            treeStructure[i] = header.charAt(index);
            index = index + 1;
        }
        String preorder = "";
        int characteri = 0;
        int itera = 0;
        for(itera = 0; itera < treeStructureSize; itera++) {
            preorder = preorder.concat(String.valueOf(treeStructure[itera]));
            if(treeStructure[itera] == '1') {
                preorder = preorder.concat(String.valueOf(characters[characteri]));
                characteri = characteri + 1;
            }
        }

        //construct tree
        //Node tree = createTree(preorder);
        return new Node();
    }

 */

    private void encode() {
        //encoding algorithm
        //pick least 2 freq and add to new node with freq = sum of freq
        while (minHeap.size() > 1) {
            NodeByte n1 = minHeap.poll();
            NodeByte n2 = minHeap.poll();
            //add new internal node to huffmanTree
            //all internal nodes have no characters and their frequency = sum of children frequencies
            int freq = 0;
            if (n1 != null) {
                freq += n1.freq;
            }
            if (n2 != null) {
                freq += n2.freq;
            }
            NodeByte temp = new NodeByte(n1, n2, null, freq);
            minHeap.add(temp);
        }

        //now root is huffmanTree
        huffmanTree = minHeap.peek();

        //encoding table
        createEncodingTable(huffmanTree, "");

        //encode the string s
        //iterate on bytes of input and replace them with their equivalent encoding
        String temp = "";
        for (Byte b : this.inputAsBytes) {
            temp = temp.concat(encodingTable.get(b));
        }
        encodedString = temp;
    }

    /*
     createEncodingTable iterates recursively on huffmanTree, whenever a left
     node is selected, 0 is concatenated on the string, whenever a right node
     is selected, 1 is concatenated on the string, until it reaches a leaf node
     where the byte in leaf node is mapped to final string s
     */
    private void createEncodingTable(NodeByte n, String s) {
        if (n == null) return;
        if (n.b != null) {
            //handling edge cases where there is only 1 node/character
            if (s.equals("")) {
                s = "0";
            }
            //put current string as value to key of current byte
            encodingTable.put(n.b, s);
            return;
        }
        createEncodingTable(n.left, s.concat("0"));
        createEncodingTable(n.right, s.concat("1"));
    }

    /*
     calculates frequency of characters using dictionary data structure
     */
    private void calculateFrequency() {
        for (Byte b : this.inputAsBytes) {
            if (this.occurrence.containsKey(b)) {
                //add 1
                Integer temp = this.occurrence.get(b);
                temp = temp + 1;
                this.occurrence.put(b, temp);
            } else {
                //1
                this.occurrence.put(b, 1);
            }
        }
    }

    /*
     sorts occurrence of bytes using minHeap, a minimum heap structure
     */
    private void sortOccurrence() {
        for (HashMap.Entry<Byte, Integer> entry : occurrence.entrySet()) {
            Byte key = entry.getKey();
            Integer value = entry.getValue();
            minHeap.add(new NodeByte(key, value));
        }
    }

    /*
     header is created as a pre-order traversal of the huffman tree
     0 represents traversing down a tree, 1 means current node is a leaf node (character)
     note that: when we arrive at 1 we read next n bytes representing character we have, so
     there's no chance that if a character has a byte representation of 1 that it would be
     mistaken for arriving at a leaf node
     */
    private void createHeader(NodeByte node) {
        if (node == null) return;

        if (node.b != null) {
            header.add((byte) 1);
            header.add(node.b);
        }

        if (node.left != null) {
            header.add((byte) 0);
            createHeader(node.left);
        }

        //here we don't add 0 since we already traversed the left node
        //so by default if we arrive to any left node then there must be
        //a right node, since huffman tree is a complete tree
        if (node.right != null)
            createHeader(node.right);
    }

    /*
     creates header as a string containing only 0, 1
     then a list of bytes has the bytes in pre-order listing
     */
    private String createHeaderStringAuxiliary(NodeByte node, String s) {
        if (node.b != null) {
            s = s.concat("1");
            charactersInPreOrder.add(node.b);
        }

        if (node.left != null) {
            s = s.concat("0");
            s = createHeaderStringAuxiliary(node.left, s);
        }

        if (node.right != null)
            s = createHeaderStringAuxiliary(node.right, s);

        return s;
    }

    /*
     new idea for header:
     |no of characters in 4 bytes| |list of characters in bytes| |size of huffman tree structure in 4 bytes| |huffman tree structure in bytes|
     */
    private void createHeader() {
        //creates huffman tree structure as string
        String s = createHeaderStringAuxiliary(this.huffmanTree, "");
        //convert huffman tree structure string to list of bytes
        treeStructureBytes = ParseString.parseString(s);

        //number of unique characters in huffman tree
        int sizeOfCharacters = charactersInPreOrder.size();

        //size of huffman tree structure
        int sizeOfStructure = treeStructureBytes.size();

        //convert number of unique characters in huffman tree from int to 4 bytes
        Byte[] tempBytes = new Byte[4];
        for (int i = 0; i < 4; i++) {
            tempBytes[3 - i] = (byte) (sizeOfCharacters >>> (i * 8));
        }
        charactersSize.addAll(Arrays.asList(tempBytes));

        //convert size of huffman tree structure from int to 4 bytes
        for (int i = 0; i < 4; i++) {
            tempBytes[3 - i] = (byte) (sizeOfStructure >>> (i * 8));
        }
        treeSizeInBytes.addAll(Arrays.asList(tempBytes));

        header.addAll(charactersSize);
        header.addAll(charactersInPreOrder);
        header.addAll(treeSizeInBytes);
        header.addAll(treeStructureBytes);
    }

    private void createEncodedFile() {
        encodedFile.addAll(header);
        //we removed the separator concept and replaced it with number of characters in 4 bytes
        //char c = 31; //unit separator between header and content
        //encodedFile.add((byte)c);
        encodedFile.addAll(encodedContent);
    }

    /*
     createTree: creates the Huffman Tree from a string s, typically converted from header in an encoded file from
     byte array to string, which represents the pre-order traversal
     of a created huffmanTree, then returns a Node that represents the root of huffman tree
     */
    //TODO change charactersInPreOrder from List<Byte> to List<List<Byte>>
    private NodeByte createTree(byte[] charactersInPreOrder, String treeStructure) {
        //temporary node is created for iterating, current is considered current node
        NodeByte current = new NodeByte(null, null, null, null, null);

        //iterating on pre-order traversal
        int j = 0; //used for iterating on list of characters
        for (int i = 0; i < treeStructure.length(); i++) {
            //if we found 1 then next byte is a character that should be stored in leaf node
            if (treeStructure.charAt(i) == '1') {
                current.b = charactersInPreOrder[j];
                j++;
                if (current.parent != null) current = current.parent;
            }

            //this node may be added either as a left or right child to curr
            NodeByte temp = new NodeByte(current, null, null, null, null);

            //if left child is null then we must have not visited it
            if (current.left == null) {
                current.left = temp;
                current = current.left;
            } else if (current.right == null) { //if right child is null then we must have not visited it
                current.right = temp;
                current = current.right;
            } else {
                //if both children are not null then we have definitely visited the left AND the right node
                //get the parent whose right child is null
                while (current.parent != null && current.left != null && current.right != null) {
                    current = current.parent;
                }
                //huffman tree is a complete tree meaning that every node except the leaf nodes have right and left child
                if (current.right == null) {
                    NodeByte right = new NodeByte(current, null, null, null, null);
                    current.right = right;
                    current = current.right;
                }
            }
        }

        //get root
        while (current.parent != null) {
            current = current.parent;
        }

        return current;
    }

    private List<Byte> convertStringIntoBytes(String encodedString) {
        return ParseString.parseString(encodedString);
    }

    private int byteToInt(byte[] b) {
        ByteBuffer bb = ByteBuffer.wrap(b);
        return bb.getInt();
    }

    public List<Byte> getEncodedFile() {
        return encodedFile;
    }

    public String getEncodedString() {
        return encodedString;
    }

    public List<Byte> getEncodedContent() {
        return encodedContent;
    }

    public HashMap<Byte, String> getEncodingTable() {
        return encodingTable;
    }

    public String getDecodedString() {
        return decodedString;
    }
}

