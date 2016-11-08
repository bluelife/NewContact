package com.oschina.bluelife.newcontact;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.oschina.bluelife.newcontact.Utils.ContactManager;
import com.oschina.bluelife.newcontact.model.OrgLogoModel;
import com.oschina.bluelife.newcontact.model.Person;
import com.oschina.bluelife.newcontact.widget.ContactFetcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by HiWin10 on 2016/10/19.
 */
@RuntimePermissions
public class EditTestFragment extends Fragment {

    private static String TAG = "edittest";
    @BindView(R.id.contact_edit)
    EditText contactEdit;
    @BindView(R.id.qr_edit)
    EditText qrEdit;
    MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_test_layout, container, false);
        ButterKnife.bind(this, view);
        mainActivity = (MainActivity) getActivity();
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("test");
        return view;
    }


    /*@OnClick(R.id.edit_test_new)
    void openAddPeople(){
        Log.w(TAG, "openAddPeople: ");
        Fragment fragment=new AddContactFragment();
        mainActivity.openFragment(fragment);
    }*/

    @OnClick(R.id.open_contact)
    void onOpenContact() {
        EditTestFragmentPermissionsDispatcher.openContactWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,Manifest.permission.CAMERA})
    void openContact() {
        String name = contactEdit.getText().toString();
        boolean hasContact = ContactManager.contactExists(getActivity().getContentResolver(), name);
        if (hasContact) {
            Bundle bundle = new Bundle();
            bundle.putString(AddExistContactFragment.KEY_NAME, name);
            Fragment fragment = new AddExistContactFragment();
            fragment.setArguments(bundle);
            mainActivity.openFragment(fragment);
        } else {
            Fragment fragment = new AddContactFragment();
            mainActivity.openFragment(fragment);
        }
    }

    @OnClick(R.id.open_contacts)
    void onOpenContacts() {
        EditTestFragmentPermissionsDispatcher.openContactsWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS})
    void openContacts() {
        Fragment fragment = new ContactListFragment();
        mainActivity.openFragment(fragment);
    }


    @OnClick(R.id.create_qrcode)
    void onOpenQRCode(){
        EditTestFragmentPermissionsDispatcher.openCreatedQRCodeWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,Manifest.permission.CAMERA})
    void openCreatedQRCode() {
        String name = qrEdit.getText().toString();

        boolean hasContact = ContactManager.contactExists(getActivity().getContentResolver(), name);
        if (hasContact) {

            Bundle bundle = new Bundle();
            bundle.putString(BusinessCardFragment.KEY_INDEX, name);

            Fragment fragment = new BusinessCardFragment();
            fragment.setArguments(bundle);
            mainActivity.openFragment(fragment);
        } else {
            mainActivity.openFragment(new EditQRcodeFragment());
        }

    }

    @OnShowRationale(Manifest.permission.CAMERA)
    void showRationaleForCamera(PermissionRequest request) {
        // NOTE: Show a rationale to explain why the permission is needed, e.g. with a dialog.
        // Call proceed() or cancel() on the provided PermissionRequest to continue or abort
        showRationaleDialog(R.string.permission_camera_rationale, request);
    }

    @OnShowRationale({Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS})
    void showRationaleForContact(PermissionRequest request) {
        // NOTE: Show a rationale to explain why the permission is needed, e.g. with a dialog.
        // Call proceed() or cancel() on the provided PermissionRequest to continue or abort
        showRationaleDialog(R.string.permission_contacts_rationale, request);
    }

    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(getContext())
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EditTestFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnClick(R.id.scan_qrcode)
    void onOpenScan() {
        EditTestFragmentPermissionsDispatcher.openScanWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void openScan() {
        Fragment fragment = new ScanQRFragment();
        mainActivity.openFragment(fragment);
    }
}
