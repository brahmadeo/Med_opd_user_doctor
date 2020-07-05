package com.razahamid.medopddoctor.Home.ui.Messages;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.razahamid.medopddoctor.Adopters.AdapterMedicineList;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.Models.MedicineModel;
import com.razahamid.medopddoctor.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddPrescriptionActivity extends AppCompatActivity {

    private TextView tvPatientName, tvPatientAge, tvPatientGender;
    private LinearLayout llProgress;
    private RecyclerView rvMedicines;
    private EditText etLabTests;

    private AdapterMedicineList adapterMedicineList;

    private final String[] frequencies = {"Daily", "Once a week", "Twice a week", "Once in 15 days", "Once a month", "4 times a day", "SOS"};
    private final String[] durations = {"Day(s)", "Week(s)", "Month(s)", "Year(s)"};
    private final String[] instructions = {"After Meal", "Before Meal", "Others"};

    private ArrayList<MedicineModel> medicines = new ArrayList<>();

    private final FirebaseRef ref = new FirebaseRef();

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private String patientUid;
    private String patientAge;
    private Boolean patientGender;
    private String chatLink;
    private String patientChatLink;
    private String patientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prescription);
        initViews();
        initFirebase();

        patientUid = getIntent().getExtras().getString(ref.DetailID);
        patientChatLink = getIntent().getExtras().getString(ref.ChatLink);
        if (Objects.equals(patientChatLink, "Aw1fe17kHiZMP0LQuvtfDgkbmbq1")) {
            chatLink = "Aw1fe17kHiZMP0LQuvtfDgkbmbq1" + "_" + firebaseUser.getUid();
            findViewById(R.id.PatientDetails).setVisibility(View.GONE);
        } else {
            if (Objects.requireNonNull(patientChatLink).compareTo(firebaseUser.getUid()) > 0) {
                chatLink = patientChatLink + "_" + firebaseUser.getUid();
            } else {
                chatLink = firebaseUser.getUid() + "_" + patientChatLink;
            }
        }
        loadPatientDetails();
    }

    private void initViews() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Prescriptions");
        toolbar.setTitleTextColor(Color.WHITE);

        tvPatientName = findViewById(R.id.tv_patient_name);
        tvPatientAge = findViewById(R.id.tv_patient_age);
        tvPatientGender = findViewById(R.id.tv_patient_gender);
        llProgress = findViewById(R.id.ll_progress);
        rvMedicines = findViewById(R.id.rv_medicines);
        etLabTests = findViewById(R.id.et_lab_tests);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapterMedicineList = new AdapterMedicineList(this, medicines);

        rvMedicines.setAdapter(adapterMedicineList);
        rvMedicines.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void initSpinners(Spinner spFrequency, Spinner spDuration, Spinner spInstructions) {
        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, frequencies);
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrequency.setAdapter(frequencyAdapter);

        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, durations);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDuration.setAdapter(durationAdapter);

        ArrayAdapter<String> instructionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, instructions);
        instructionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spInstructions.setAdapter(instructionAdapter);
    }

    private void initFirebase() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void loadPatientDetails() {
        setProgress(true);
        FirebaseFirestore.getInstance().collection(ref.PatientDetails).document(patientUid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    patientName = documentSnapshot.getString(ref.PName);
                    patientAge = documentSnapshot.getString(ref.PAge);
                    patientGender = documentSnapshot.getBoolean(ref.Gender);
                    runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    tvPatientName.setText(patientName);
                                    tvPatientAge.setText(patientAge);
                                    tvPatientGender.setText(patientGender ? "Male" : "Female");
                                }
                            }
                    );
                }
                setProgress(false);
            }
        });
    }

    private void onAddToMedicine(MedicineModel model) {
        medicines.add(model);
        if (adapterMedicineList != null)
            adapterMedicineList.notifyDataSetChanged();
    }

    private boolean validateEditText(EditText editText, @Nullable String msg) {
        if (msg == null || msg.isEmpty())
            msg = "Please fill this field";
        if (editText == null) {
            return false;
        }
        if (editText.getText().toString().isEmpty()) {
            editText.setError(msg);
            return false;
        }
        return true;
    }

    public void onAddMedicineClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.add_medicine_item, null, false);

        final EditText etMedicine = view.findViewById(R.id.et_medicine_name);
        final EditText etDosageOne = view.findViewById(R.id.et_dosage_one);
        final EditText etDosageTwo = view.findViewById(R.id.et_dosage_two);
        final EditText etDosageThree = view.findViewById(R.id.et_dosage_three);
        final EditText etDurationCount = view.findViewById(R.id.et_duration);
        final Spinner spFrequency = view.findViewById(R.id.sp_frequency);
        final Spinner spDuration = view.findViewById(R.id.sp_duration);
        final Spinner spInstruction = view.findViewById(R.id.sp_instructions);


        initSpinners(spFrequency, spDuration, spInstruction);

        builder.setView(view);
        final AlertDialog dialog = builder.create();

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }
        });

        view.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateEditText(etMedicine, null)) {
                    return;
                }
                if (!validateEditText(etDosageOne, null)) {
                    return;
                }

                if (!validateEditText(etDosageTwo, null)) {
                    return;
                }

                if (!validateEditText(etDosageThree, null)) {
                    return;
                }

                if (!validateEditText(etDurationCount, null)) {
                    return;
                }

                MedicineModel model = new MedicineModel();
                model.medicineName = etMedicine.getText().toString();
                model.dosageOne = etDosageOne.getText().toString();
                model.dosageTwo = etDosageTwo.getText().toString();
                model.dosageThree = etDosageThree.getText().toString();
                model.frequency = frequencies[spFrequency.getSelectedItemPosition()];
                model.durationCount = etDurationCount.getText().toString();
                model.durationLabel = durations[spDuration.getSelectedItemPosition()];
                model.instruction = instructions[spInstruction.getSelectedItemPosition()];
                onAddToMedicine(model);
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void onCancel(View v) {
        this.finish();
    }


    public void onSave(View v) {
        if (medicines.size() > 0) {
            setProgress(true);
            final String labTests = etLabTests.getText().toString();

            firebaseFirestore = FirebaseFirestore.getInstance();
            FirebaseFirestore.getInstance().collection(ref.Users).document(firebaseUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot doctorDocumentSnap, @Nullable FirebaseFirestoreException e) {

                    final DocumentReference prescriptionRef = firebaseFirestore.collection(ref.Prescriptions).document();
                    Map<String, Object> presHeaderData = new HashMap<>();
                    presHeaderData.put(ref.PName, patientName);
                    presHeaderData.put(ref.PAge, patientAge);
                    presHeaderData.put(ref.Gender, patientGender);
                    presHeaderData.put(ref.Patient, patientUid);
                    presHeaderData.put(ref.DoctorId, firebaseUser.getUid());
                    presHeaderData.put(ref.LabTests, labTests);
                    presHeaderData.put(ref.DoctorName, doctorDocumentSnap.getString(ref.FirstName));
                    presHeaderData.put(ref.TimeStamp, FieldValue.serverTimestamp());
                    ArrayList<HashMap<String, Object>> medicinesData = new ArrayList<>();

                    // Add the medicines
                    for (int i = 0; i < medicines.size(); i++) {
                        HashMap<String, Object> medicineData = new HashMap<>();
                        medicineData.put(ref.MedicineName, medicines.get(i).medicineName);
                        medicineData.put(ref.DosageOne, medicines.get(i).dosageOne);
                        medicineData.put(ref.DosageTwo, medicines.get(i).dosageTwo);
                        medicineData.put(ref.DosageThree, medicines.get(i).dosageThree);
                        medicineData.put(ref.DurationCount, medicines.get(i).durationCount);
                        medicineData.put(ref.DurationPeriod, medicines.get(i).durationLabel);
                        medicineData.put(ref.Frequency, medicines.get(i).frequency);
                        medicineData.put(ref.Instruction, medicines.get(i).instruction);

                        medicinesData.add(medicineData);
                    }
                    presHeaderData.put(ref.Medicines, medicinesData);

                    prescriptionRef.set(presHeaderData)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                    setProgress(false);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(AddPrescriptionActivity.this,
                                                    "Unable to save the prescription. Please try again.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Send as firebase chat
                                    sendFirebaseMessage(prescriptionRef.getId());
                                }
                            });
                }
            });


        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AddPrescriptionActivity.this,
                            "Please add the medicine and try again.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendFirebaseMessage(String firestoreId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(ref.ChatMessages + "/" + chatLink + "/" + ref.Chat);
        Map<String, Object> user = new HashMap<>();
        user.put(ref.Sender, firebaseUser.getUid());
        user.put(ref.Message, "Doctor send a Prescription");
        user.put(ref.Receiver, patientChatLink);
        user.put(ref.TimeStamp, ServerValue.TIMESTAMP);
        user.put(ref.MessageType, ref.Prescriptions);
        user.put(ref.PrescriptionId, firestoreId);
        databaseReference.push().setValue(user);
        if (Objects.equals(patientChatLink, "Aw1fe17kHiZMP0LQuvtfDgkbmbq1")) {
            chatLink = "Aw1fe17kHiZMP0LQuvtfDgkbmbq1" + "_" + firebaseUser.getUid();
            findViewById(R.id.PatientDetails).setVisibility(View.GONE);
        } else {
            if (Objects.requireNonNull(patientChatLink).compareTo(firebaseUser.getUid()) > 0) {
                chatLink = patientChatLink + "_" + firebaseUser.getUid();
            } else {
                chatLink = firebaseUser.getUid() + "_" + patientChatLink;
            }
        }
        FirebaseDatabase.getInstance().getReference(ref.LastMessage + "/" + firebaseUser.getUid() + "/" + patientChatLink).updateChildren(user);
        FirebaseDatabase.getInstance().getReference(ref.LastMessage + "/" + patientChatLink + "/" + firebaseUser.getUid()).updateChildren(user);

        setProgress(false);
        finish();
    }

    private void setProgress(final Boolean isShow) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                llProgress.setVisibility(isShow ? View.VISIBLE : View.GONE);
            }
        });
    }


}
