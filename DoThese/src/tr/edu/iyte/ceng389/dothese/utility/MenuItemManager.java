package tr.edu.iyte.ceng389.dothese.utility;

import android.view.Menu;
import android.view.MenuItem;

/** A utility class for enabling and disabling ActionBar menu items
 * 
 * @author ezgihacihalil - 180201058
 * @author mehmetakiftutuncu - 170201018 */
public class MenuItemManager
{
	/** Changes the status of a given ActionBar menu item */
	public static void setItemEnabled(Menu menu, int itemId, boolean isEnabled)
	{
		if(menu != null)
		{
			MenuItem item = menu.findItem(itemId);
			if(item != null)
				item.setVisible(isEnabled);
		}
	}
}