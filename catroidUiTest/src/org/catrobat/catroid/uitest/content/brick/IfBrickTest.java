/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.content.brick;

import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

public class IfBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private Project project;
	private IfLogicBeginBrick ifBrick;

	public IfBrickTest() {
		super(ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	@Smoke
	public void testIfBrick() {
		ListView view = UiTestUtils.getScriptListView(solo);
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) view.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		UiTestUtils.testBrickWithFormulaEditor(solo, 0, 1, 5, "ifCondition", ifBrick);

		assertEquals("Incorrect number of bricks.", 3 + 1, dragDropListView.getChildCount()); // don't forget the footer
		assertEquals("Incorrect number of bricks.", 3, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.brick_if_begin)));
	}

	public void testStrings() {

		solo.clickOnEditText(0);
		solo.sleep(100);

		boolean isFound = solo.searchText(solo.getString(R.string.brick_if_begin_second_part));
		assertTrue("String: " + getActivity().getString(R.string.brick_if_begin_second_part) + " not found!", isFound);

		isFound = solo.searchText(solo.getString(R.string.brick_if_begin));
		assertTrue("String: " + getActivity().getString(R.string.brick_if_begin) + " not found!", isFound);

		solo.goBack();
		solo.goBack();
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		ifBrick = new IfLogicBeginBrick(sprite, 0);
		IfLogicElseBrick ifElseBrick = new IfLogicElseBrick(sprite, ifBrick);
		IfLogicEndBrick ifEndBrick = new IfLogicEndBrick(sprite, ifElseBrick, ifBrick);
		ifBrick.setElseBrick(ifElseBrick);
		ifBrick.setEndBrick(ifEndBrick);
		script.addBrick(ifBrick);
		script.addBrick(ifElseBrick);
		script.addBrick(ifEndBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
