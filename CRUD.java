package il.co.ilrd.factorypnp;

public interface CRUD <ID,D> {
	public ID create(D data);
	public D read(ID id);
	public void update(ID id, D date);
	public void delete(ID id);
}
