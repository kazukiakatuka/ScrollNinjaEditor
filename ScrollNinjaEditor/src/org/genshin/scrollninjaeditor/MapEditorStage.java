package org.genshin.scrollninjaeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class MapEditorStage extends Stage{
	private ScrollPaneStage scrolloPaneStage;
	private Table			table;
	private Import			importButton;
	private Export			exportButton;
	private MenuButton		menuButton;
	private Camera			camera2;
	private LayerManager    layermanager;
	private Label			scale;
	private Table			scrollTable;
	private int 			sizeCnt = 0;
	
	private float z = 1.0f;
	
	/**
	 * Create
	 * @param fileName
	 * @param load
	 */
	public MapEditorStage(String fileName, Load load){
		super();
		scrolloPaneStage = new ScrollPaneStage();
		table = new Table();
		table.setFillParent(true);
		table.debug();
		scrollTable = new Table();
		scrollTable.setFillParent(true);
		scrollTable.right();
		scrollTable.debug();
		
		scale = new Label("",new Skin(Gdx.files.internal("data/uiskin.json")));
		importButton = new Import(load.getSpriteDrawable(Load.IMPORT));
		exportButton = new Export(load.getSpriteDrawable(Load.EXPORT));
		menuButton = new MenuButton(load.getSpriteDrawable(Load.MENU));
	}
	
	/**
	 * create
	 * @param screenWidth
	 * @param screenHeight
	 * @param manager
	 * @param camera
	 */
	public void create(float screenWidth ,float screenHeight,final MapObjectManager manager,Camera camera,LayerManager layer){
		layermanager = layer;
		importButton.setlayer(layermanager);
		exportButton.setlayer(layermanager);
		createScrollPane(manager,camera);
		menuButton.create(table, screenWidth, this);
		addButton(screenWidth, screenHeight);
		camera2 = camera;
		scale.setColor(0.0f, 0.0f, 0.0f, 1.0f);
		scale.setPosition(64.0f,screenHeight - 32);
		scale.setText(Math.round(100 + (-sizeCnt  * 10))  + "%");
		this.addActor(scale);
		// スクロール
		scroll();

	}
	
	private void scroll() {
		addListener(new InputListener(){
			@Override
			public boolean handle (Event e) {
			if (!(e instanceof InputEvent)) return false;
				InputEvent event = (InputEvent)e;
				if (event.getType() == InputEvent.Type.scrolled) {
					sizeCnt += event.getScrollAmount();
					if(sizeCnt < -9)
						sizeCnt = -9;
					else if(sizeCnt > 9)
						sizeCnt = 9;
					
					if(sizeCnt <= 0) {
						z = 1.0f + (sizeCnt * 0.1f);
						if(z < 0.1f) {
							z = 0.1f;
						}
					}
		
					else if(sizeCnt <= 5) {
						z = 1.1f + (0.025f * sizeCnt * sizeCnt) + (0.1f * sizeCnt);
					}
					else if(sizeCnt < 10) {
						z = 0.625f + (0.1875f * sizeCnt * sizeCnt) + (-0.5625f * sizeCnt);
						if(z > 10.0f)
							z = 10.0f;
					}
						
					scale.setText(Math.round(100 + (-sizeCnt  * 10))  + "%");
				
	
				}
		
				return true;
			}
			});
	}
	
	public float getZoom(float texWidth,float texHeight ){
		float screenWidth = Gdx.graphics.getWidth();
		float screenHeight = Gdx.graphics.getHeight();
		if((Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT)) && Gdx.input.isKeyPressed(Keys.NUM_0)){
			if(texWidth / screenWidth < texHeight / screenHeight)
				return texHeight / screenHeight;
			else
				return texWidth / screenWidth;
		}
		return z ;
	}

	/**
	 * addButton
	 * @param screenWidth
	 * @param screenHeight
	 */
	public void addButton(final float screenWidth ,final float screenHeight){
		// インポート
		table.add(importButton).top().left().size(32,32);
		
		// エクスポート
		table.add(exportButton).top().left().size(32,32);
		
		// メニュー
		table.add(menuButton).expand().right().top();
		addActor(table);
	}
	
	/**
	 * createScrollPane
	 * @param manager
	 * @param camera
	 */
	public void createScrollPane(final MapObjectManager manager,Camera camera){
		scrolloPaneStage.menuCreate(manager, camera,layermanager,scrollTable);
		scrollTable.row();
		scrolloPaneStage.layerFrontCreate(scrollTable);
		addActor(scrollTable);
		scrolloPaneStage.layerBackCreate(scrollTable);
		addActor(scrollTable);
		removeButton();
	}
	
	/**
	 * AddScrollPane process
	 */
	public void addScrollPane(){
		scrolloPaneStage.layerFrontCreate(scrollTable);
		addActor(scrollTable);
	}

	/**
	 * Remove process
	 */
	public void removeButton(){
		getRoot().removeActor(scrollTable);
	}

	/**
	 * getScrollPaneWidth
	 * @return scrollPane.getWidth()
	 */
	public float getPaneWidth(){
		return scrolloPaneStage.getScrollPaneWidth();
	}
}
