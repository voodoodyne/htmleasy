package com.googlecode.htmleasy;


/**
 * Like View, but allows you to specify a model as well.  The model will be put
 * in the request attributes.
 * 
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
public class ViewAndModel extends View
{
	protected Object model;
	protected String modelName;
	
	/**
	 * The default model name is "model".
	 * 
	 * @see ViewAndModel(String, Object, String)
	 */
	public ViewAndModel(String path, Object model)
	{
		this(path, model, "model");
	}
	
	/**
	 * @param path will be dispatched to using the servlet container; it should
	 *  have a leading /
	 * @param model will be put in the request attrs
	 * @param modelName will be the key in the request attrs of the model
	 */
	public ViewAndModel(String path, Object model, String modelName)
	{
		super(path);
		
		this.model = model;
		this.modelName = modelName;
	}
	
	/** */
	public Object getModel() { return this.model; }
	
	/** */
	public String getModelName() { return this.modelName; }
}
