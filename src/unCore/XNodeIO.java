package unCore;

import java.util.function.Consumer;

public interface XNodeIO<T> {
	
	public T getData();
	public void setData( T input );
	
	public default void modifyData( Consumer<T> modifyFn ) {
		modifyFn.accept( getData() );
	}
	
}
