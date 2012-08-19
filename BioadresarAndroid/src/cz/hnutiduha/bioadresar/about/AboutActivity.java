/*  This file is part of BioAdresar.
	Copyright 2012 Jiri Zouhar (zouhar@trilobajt.cz)

    BioAdresar is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BioAdresar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with BioAdresar.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.hnutiduha.bioadresar.about;

import cz.hnutiduha.bioadresar.R;
import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_view);
        
        View item = this.findViewById(R.id.webText);
        Linkify.addLinks((TextView)item, Linkify.WEB_URLS);
        item = this.findViewById(R.id.emailText);
        Linkify.addLinks((TextView)item, Linkify.EMAIL_ADDRESSES);
        
        item = this.findViewById(R.id.attributionText);
        Linkify.addLinks((TextView)item, Linkify.WEB_URLS);
        item = this.findViewById(R.id.licenceText);
        Linkify.addLinks((TextView)item, Linkify.WEB_URLS);
    }
}
