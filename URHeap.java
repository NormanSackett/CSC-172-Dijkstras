public class URHeap<T extends Comparable<T>> {
	T[] heapArr;
	int size, capacity = 0;
	
	@SuppressWarnings("unchecked")
	public URHeap(int c) {
		capacity = c;
		heapArr = (T[]) new Comparable[capacity];
	}
	
	@SuppressWarnings("unchecked")
	public URHeap() {
		capacity = 10;
		heapArr = (T[]) new Comparable[capacity];
	}
	
	public void printHeap() {
		int currentLevel = 0;
		for (int i = 0; i < size; i++) {
			int iLog = (int) (Math.log(i + 1)/Math.log(2));
			
			if (iLog > currentLevel) {
				currentLevel++;
				System.out.print("\n");
			}
			System.out.print(heapArr[i] + " ");
		}
	}
	
	public void insert(T item) {
		if (size >= capacity) {
			resize();
		}
		heapArr[size++] = item;
		if (size() > 1)
			bubbleUp();
	}
	
	private void bubbleUp() {
		int index = size - 1;
		T insert = heapArr[index]; //inserted item
		
		//because this is a min heap, if the inserted item is less than its parent, the two are swapped
		while (insert.compareTo(heapArr[(index - 1) / 2]) < 0 && index != 0) {
			swap(index, (index - 1) / 2);
			index = (index - 1) / 2;
		}
	}
	
	//swap elements of heapArr at the given indicies
	private void swap(int i1, int i2) {
		T tempItem = heapArr[i1];
		heapArr[i1] = heapArr[i2];
		heapArr[i2] = tempItem;
	}
	
	@SuppressWarnings("unchecked")
	private void resize() {
		T[] tempArr = heapArr.clone();
		//reinstantiate heapArr
		capacity *= 2;
		heapArr = (T[]) new Comparable[capacity];
		
		//transcribe the temporary array back to the heap array
		for (int i = 0; i < tempArr.length; i++) {
			heapArr[i] = tempArr[i];
		}
	}

	public boolean isEmpty() {
		if (size == 0) return true;
		
		return false;
	}

	public int size() {
		return size;
	}

	public T deleteMin() {
		if (heapArr[0] != null) {
			size--;
			T min = heapArr[0];
			heapArr[0] = heapArr[size];
			heapArr[size] = null;
		
			bubbleDown();
		
			return min;
		} else return null;
	}
	
	public void clear() {
		while (!isEmpty()) {
			deleteMin();
		}
	}
	
	private void bubbleDown() {
		int index = 0;
		T root = heapArr[0]; //new root of the heap, must be brought down
		
		//because this is a min heap, if the child is less than its parent, the two swap
		while ((2 * index) + 2 < size) {
			int compareIndex;
			if (heapArr[(2 * index) + 1].compareTo(heapArr[(2 * index) + 2]) < 0) //compare the parent to the less of the two children
				compareIndex = (2 * index) + 1;
			else
				compareIndex = (2 * index) + 2;
			
			if (heapArr[compareIndex].compareTo(root) < 0) { //swap parent and child if the child is smaller
				swap(index, compareIndex);
				index = compareIndex;
			} else {
				break;
			}
		}
	}
}
