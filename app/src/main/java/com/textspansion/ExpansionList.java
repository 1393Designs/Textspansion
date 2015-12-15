package com.textspansion;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.textspansion.R;
import com.textspansion.domain.Expansion;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ExpansionList extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.fab) FloatingActionButton fab;
    @Bind(R.id.drawer_layout) DrawerLayout drawer;
    @Bind(R.id.nav_view) NavigationView navigationView;
    @Bind(R.id.expansions_recycler_view) RecyclerView expansionsRecyclerView;
    private RecyclerView.LayoutManager expansionsRecyclerLayoutManager;

    private static List<Expansion> seedExpansions = Arrays.asList(
            new Expansion("crm", "Caramels", "goooey"),
            new Expansion("marshie", "marshmallow", "white"),
            new Expansion("pie", "3.1415927", "pi"),
            new Expansion("ck", "cheesecake", "meh"),
            new Expansion("marshie2", "marshmallow again!", "still white"),
            new Expansion("hlva", "halvah", "dunno"),
            new Expansion("dnt", "donut", "super delicious"),
            new Expansion("bc", "bear claw", "omg I want one"),
            new Expansion("long", "Cupcake ipsum dolor. Sit amet chocolate cake biscuit icing tiramisu cheesecake. Tart gingerbread jelly powder jelly dessert tiramisu. Toffee wafer liquorice cupcake danish. Powder cotton candy lollipop candy canes jelly-o macaroon. Sugar plum danish dessert. Sugar plum marshmallow cake pastry fruitcake chocolate bar. Croissant jujubes halvah. Cheesecake bear claw apple pie apple pie pastry. Halvah apple pie tart wafer lemon drops. Liquorice cotton candy jelly beans apple pie dessert jelly-o pastry danish danish. Toffee cake lemon drops cupcake cake. Lollipop oat cake sugar plum jujubes pie apple pie apple pie sugar plum tart.", "whoops"),
            new Expansion("gmy", "gummies", "bears?"),
            new Expansion("fc", "fruitcake", "never had it to be honest")
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expansion_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        // changes in the number of expansions don't change the size of the layout view
        expansionsRecyclerView.setHasFixedSize(true);
        expansionsRecyclerLayoutManager = new LinearLayoutManager(this);
        expansionsRecyclerView.setLayoutManager(expansionsRecyclerLayoutManager);

        ExpansionAdapter expansionAdapter = new ExpansionAdapter(seedExpansions);
        expansionsRecyclerView.setAdapter(expansionAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Hello, Butterknife!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
