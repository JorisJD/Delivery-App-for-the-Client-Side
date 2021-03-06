package com.example.sirojiddinjumaev.niholeatit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.sirojiddinjumaev.niholeatit.Common.Common;
import com.example.sirojiddinjumaev.niholeatit.Database.Database;
import com.example.sirojiddinjumaev.niholeatit.Interface.ItemClickListener;
import com.example.sirojiddinjumaev.niholeatit.Model.Banner;
import com.example.sirojiddinjumaev.niholeatit.Model.Category;
import com.example.sirojiddinjumaev.niholeatit.Model.Favorites;
import com.example.sirojiddinjumaev.niholeatit.Model.Token;
import com.example.sirojiddinjumaev.niholeatit.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    FirebaseDatabase database;
    DatabaseReference category;

    TextView txtFullName;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;

    SwipeRefreshLayout swipeRefreshLayout;

    CounterFab fab;

    //Slider
    HashMap<String, String> image_list;
    SliderLayout mSlider;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Note: add this code before setContentView method
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/restaurant_font.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_home);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        //View
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (Common.IsConnectedToInterner(getBaseContext()))


                    loadMenu();

                else
                {
                    Toast.makeText(getBaseContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        //Default, load for first time
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.IsConnectedToInterner(getBaseContext()))


                    loadMenu();

                else
                {
                    Toast.makeText(getBaseContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });


        //init  Firebase

        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        FirebaseRecyclerOptions<Category> options =new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category.class)
                .build();


        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {

            @Override
            public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);
                return new MenuViewHolder(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, int position, @NonNull Category model) {

                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);
                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Get Category ID and send to new Activity
                        Intent foodList = new Intent(Home.this, FoodList.class);

                        //Because Category ID is key, so we just get key of this item
                        foodList.putExtra("Category", adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });

            }
        };

        Paper.init(this);

        fab = (CounterFab) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Home.this, Cart.class);
                startActivity(cartIntent);
            }
        });

        fab.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //set Name for User
        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView) headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getName());


        //Load Menu
        recycler_menu = (RecyclerView) findViewById(R.id.recycler_menu);

      //  layoutManager = new LinearLayoutManager(this);
     //   recycler_menu.setLayoutManager(layoutManager);
        recycler_menu.setLayoutManager(new GridLayoutManager(this, 2));

        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler_menu.getContext(),
                R.anim.layout_fall_down);
        recycler_menu.setLayoutAnimation(controller);




        updateToken(FirebaseInstanceId.getInstance().getToken());

        //Setup SLIDER
        //Need call this function after you init database firebase
        setupSlider();


    }

    private void setupSlider() {
        mSlider = (SliderLayout)findViewById(R.id.slider);
        image_list = new HashMap<>();

        final DatabaseReference banners = database.getReference("Banner");

        banners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    Banner banner = postSnapShot.getValue(Banner.class);
                    //We will connect String name and id like
                    //PIZZA_01 => And we will use PIZZA for show description, 01 food for id to click
                    image_list.put(banner.getName()+"@@@"+banner.getId(),banner.getImage());
                }

                for (String key:image_list.keySet())
                {
                    String[] keySplit = key.split("@@@");
                    String nameOfFood = keySplit[0];
                    String idOfFood = keySplit[1];

                    //Create Slider
                    final TextSliderView textSliderView = new TextSliderView(getBaseContext());
                    textSliderView
                            .description(nameOfFood)
                            .image(image_list.get(key))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent = new Intent(Home.this, FoodDetail.class);
                                    //We will send food id to FoodDetail
                                    intent.putExtras(textSliderView.getBundle());
                                    startActivity(intent);
                                }
                            });

                    //Add extra Bundle
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("FoodId", idOfFood);

                    mSlider.addSlider(textSliderView);

                    //Remove event after finish

                    banners.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(4000);
    }

    private void updateToken(String token) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token, false); //false because this token send from client side
        tokens.child(Common.currentUser.getPhone()).setValue(data);

    }


    private void loadMenu() {


         adapter.startListening();
        recycler_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        //Animation
        recycler_menu.getAdapter().notifyDataSetChanged();
        recycler_menu.scheduleLayoutAnimation();
    }

    //CTRL+O


    @Override
    protected void onStop() {
        super.onStop();
        if(adapter !=null)
            adapter.stopListening();
        mSlider.stopAutoCycle();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.menu_search)
            startActivity(new Intent(Home.this, SearchActivity.class));

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {

            Intent cartIntent = new Intent(Home.this, Cart.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_orders) {

            Intent orderIntent = new Intent(Home.this, OrderStatus.class);
            startActivity(orderIntent);

        } else if (id == R.id.nav_log_out) {

            //Delete Remember user and password
            Paper.book().destroy();

            //LogOut
            Intent signIn = new Intent(Home.this, SignIn.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);

        } else if (id == R.id.nav_change_pwd)
        {
            showChangePasswordDialog();
        }

        else if (id == R.id.nav_home_address)
        {
            showHomeAddressDialog();
        }
        else if (id == R.id.nav_setting)
        {
            showSettingDialog();
        }
        else if (id == R.id.nav_favorites)
        {
            startActivity(new Intent(Home.this, FavoritesActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showSettingDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("SETTINGS");


        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_setting = inflater.inflate(R.layout.setting_layout, null);


        final CheckBox ckb_subscribe_new = (CheckBox) layout_setting.findViewById(R.id.ckb_sub_new);

        //Add code remember state of CheckBox
        Paper.init(this);
        String isSubscribe = Paper.book().read("sub_new");
        if (isSubscribe == null || TextUtils.isEmpty(isSubscribe) || isSubscribe.equals("false"))
            ckb_subscribe_new.setChecked(false);
        else
            ckb_subscribe_new.setChecked(true);

        alertDialog.setView(layout_setting);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                if (ckb_subscribe_new.isChecked())
                {
                    FirebaseMessaging.getInstance().subscribeToTopic(Common.topicName);
                    //Write value
                    Paper.book().write("sub_new", true);
                }
                else
                {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Common.topicName);
                    //Write value
                    Paper.book().write("sub_new", false);

                }


            }
        });
        alertDialog.show();

    }

    private void showHomeAddressDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("CHANGE HOME ADDRESS");
        alertDialog.setMessage("Please fill all information");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_home = inflater.inflate(R.layout.home_address_layout, null);

        final MaterialEditText edtHomeAddress = (MaterialEditText) layout_home.findViewById(R.id.edtHomeAddress);

        alertDialog.setView(layout_home);

        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();


                //Set New Home Address
                Common.currentUser.setHomeAddress(edtHomeAddress.getText().toString());

                FirebaseDatabase.getInstance().getReference("User")
                        .child(Common.currentUser.getPhone())
                        .setValue(Common.currentUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Home.this, "Update /" +
                                        "Address Successful", Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });
        alertDialog.show();

    }

    private void showChangePasswordDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("CHANGE PASSWORD");
        alertDialog.setMessage("Please fill all information");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_pwd = inflater.inflate(R.layout.change_password_layout, null);

        final MaterialEditText edtPassword = (MaterialEditText) layout_pwd.findViewById(R.id.edtPassword);
        final MaterialEditText edtNewPassword = (MaterialEditText) layout_pwd.findViewById(R.id.edtNewPassword);
        final MaterialEditText edtRepeatPassword = (MaterialEditText) layout_pwd.findViewById(R.id.edtRepeatPassword);

        alertDialog.setView(layout_pwd);

        //Button
        alertDialog.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Change Password here

                //For use SpotsDialog, please use AlerDialog from android.app, not from v7 like above AlertDialog
                final android.app.AlertDialog waitingDialog = new SpotsDialog(Home.this);
                waitingDialog.show();

                //Check old Password
                if (edtPassword.getText().toString().equals(Common.currentUser.getPassword()))
                {
                    //Check new Password and Repeat Password
                    if (edtNewPassword.getText().toString().equals(edtRepeatPassword.getText().toString()))
                    {
                        Map<String, Object> passwordUpdate = new HashMap<>();
                        passwordUpdate.put("Password", edtNewPassword.getText().toString());

                        //Make Update
                        DatabaseReference user = FirebaseDatabase.getInstance().getReference("User");
                        user.child(Common.currentUser.getPhone())
                                .updateChildren(passwordUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        waitingDialog.dismiss();

                                        Toast.makeText(Home.this, "Password was Update", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else
                    {
                        waitingDialog.dismiss();
                        Toast.makeText(Home.this, "New Password does not match", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    waitingDialog.dismiss();
                    Toast.makeText(Home.this, "Wrong Old Password", Toast.LENGTH_SHORT).show();
                }

            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();


    }
    @Override
    protected void onResume() {
        super.onResume();
       // loadMenu();
        if(adapter !=null)
            adapter.startListening();
        fab.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

    }
}
