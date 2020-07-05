package com.razahamid.medopd.Adopters;

import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.razahamid.medopd.ExtraFiles.FirebaseRef;
import com.razahamid.medopd.ExtraFiles.FullscreenActivity;
import com.razahamid.medopd.Models.MedicineModel;
import com.razahamid.medopd.Models.Message;
import com.razahamid.medopd.R;
import com.razahamid.medopd.utils.Tools;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdapterChat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int CHAT_ME = 100;
    private final int CHAT_YOU = 200;
    private final int CHAT_PRESCRIPTION = 300;
    private FirebaseRef ref=new FirebaseRef();
    private List<Message> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    public interface OnItemClickListener {
        void onItemClick(View view, Message obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterChat(Context context) {
        ctx = context;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView text_content;
        public TextView text_time;
        public View lyt_parent;
        public ImageView image;

        public ItemViewHolder(View v) {
            super(v);
            text_content = v.findViewById(R.id.text_content);
            text_time = v.findViewById(R.id.text_time);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            image=v.findViewById(R.id.image);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == CHAT_PRESCRIPTION) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_prescription_item, parent, false);
            vh = new PrescriptionViewHolder(v);
        } else
        if (viewType == CHAT_ME) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_me, parent, false);
            vh = new ItemViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_you, parent, false);
            vh = new ItemViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final Message m = items.get(position);
            ItemViewHolder vItem = (ItemViewHolder) holder;
            vItem.text_content.setText(m.dataSnapshot.child(ref.Message).getValue(String.class));
            vItem.text_time.setText(Tools.getFormattedTimeEvent(m.dataSnapshot.child(ref.TimeStamp).getValue(Long.class)));
            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, m, position);
                    }
                }
            });
            if (m.dataSnapshot.hasChild(ref.ImageUrl)){
                vItem.image.setVisibility(View.VISIBLE);
                Glide.with(ctx).load(m.dataSnapshot.child(ref.ImageUrl).getValue(String.class)).into(vItem.image);
                vItem.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle=new Bundle();
                        bundle.putString(ref.ImageUrl,m.dataSnapshot.child(ref.ImageUrl).getValue(String.class));
                        ctx.startActivity(new Intent(ctx, FullscreenActivity.class).putExtras(bundle));
                    }
                });
            }

            else {
                vItem.image.setVisibility(View.GONE);
            }
        }   else if (holder instanceof PrescriptionViewHolder) {
            final Message m = items.get(position);
            final PrescriptionViewHolder viewHolder = (PrescriptionViewHolder) holder;
            final String prescriptionId = m.dataSnapshot.child(ref.PrescriptionId).getValue(String.class);
            if (prescriptionId != null && !prescriptionId.isEmpty()) {
                viewHolder.llProgress.setVisibility(View.VISIBLE);

                FirebaseFirestore.getInstance()
                        .collection(ref.Prescriptions)
                        .document(prescriptionId)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy, HH:mm");
                                final String date = sfd.format(documentSnapshot.getTimestamp(ref.TimeStamp).toDate());
                                final String patientName = documentSnapshot.getString(ref.PName);
                                final String patientAge = documentSnapshot.getString(ref.PAge);
                                final String patientGender = documentSnapshot.getBoolean(ref.Gender) ? "Male" : "Female";
                                String doctorName = "";
                                if (documentSnapshot.contains(ref.DoctorName))
                                    doctorName = documentSnapshot.getString(ref.DoctorName);

                                String labTests = "";
                                if (documentSnapshot.contains(ref.LabTests))
                                    labTests = documentSnapshot.getString(ref.LabTests);

                                viewHolder.tvDate.setText(date);
                                viewHolder.tvPatientName.setText(patientName);
                                viewHolder.tvPatientAge.setText(patientAge);
                                viewHolder.tvPatientGender.setText(patientGender);
                                viewHolder.tvDoctorName.setText(doctorName);
                                viewHolder.tvLabTests.setText(labTests);

                                final ArrayList<MedicineModel> medicines = new ArrayList<>();
                                for (HashMap<String, Object> medicineMap : (ArrayList<HashMap<String, Object>>) documentSnapshot.get(ref.Medicines)) {
                                    MedicineModel medicine = new MedicineModel();
                                    medicine.medicineName = (String) medicineMap.get(ref.MedicineName);
                                    medicine.dosageOne = (String) medicineMap.get(ref.DosageOne);
                                    medicine.dosageTwo = (String) medicineMap.get(ref.DosageTwo);
                                    medicine.dosageThree = (String) medicineMap.get(ref.DosageThree);
                                    medicine.durationCount = (String) medicineMap.get(ref.DurationCount);
                                    medicine.durationLabel = (String) medicineMap.get(ref.DurationPeriod);
                                    medicine.frequency = (String) medicineMap.get(ref.Frequency);
                                    medicine.instruction = (String) medicineMap.get(ref.Instruction);
                                    medicines.add(medicine);
                                }
                                viewHolder.rvMedicines.setAdapter(new AdapterMedicineList(ctx, medicines, false));
                                viewHolder.rvMedicines.setLayoutManager(new LinearLayoutManager(ctx, RecyclerView.VERTICAL, false));
                                viewHolder.llProgress.setVisibility(View.GONE);
                                viewHolder.btnShare.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        viewHolder.btnShare.setVisibility(View.GONE);
                                        generateAndSharePDF(viewHolder.lytParent, date, patientName, patientAge, patientGender, medicines);
                                        viewHolder.btnShare.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        });
            }
        }

    }


    private void generateAndSharePDF(View v,String date, String patientName, String patientAge, String patientGender, ArrayList<MedicineModel> medicines){
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.Page page = pdfDocument.startPage(new PdfDocument.PageInfo.Builder(v.getWidth(), v.getHeight(), 0).create());

        v.draw(page.getCanvas());
        pdfDocument.finishPage(page);
        File file = new File(ctx.getFilesDir().getAbsolutePath()+ "/test.pdf");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            pdfDocument.writeTo(outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        pdfDocument.close();
        Uri uri = FileProvider.getUriForFile(ctx,"com.razahamid.medopd.provider",file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        intent.putExtra(Intent.EXTRA_SUBJECT,
                "Sharing File...");
        intent.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

        ctx.startActivity(intent);

    }



    public class PrescriptionViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate, tvPatientName, tvPatientGender, tvPatientAge, tvDoctorName, tvLabTests;
        private LinearLayout llProgress, lytParent;
        private RecyclerView rvMedicines;
        private Button btnShare;

        public PrescriptionViewHolder(@NonNull View v) {
            super(v);
            tvDate = v.findViewById(R.id.tv_date);
            tvPatientName = v.findViewById(R.id.tv_patient_name);
            tvPatientGender = v.findViewById(R.id.tv_patient_gender);
            tvPatientAge = v.findViewById(R.id.tv_patient_age);
            llProgress = v.findViewById(R.id.ll_progress);
            rvMedicines = v.findViewById(R.id.rv_medicines);
            btnShare = v.findViewById(R.id.btn_share);
            lytParent = v.findViewById(R.id.lyt_parent);
            tvDoctorName = v.findViewById(R.id.tv_doctor_name);
            tvLabTests = v.findViewById(R.id.tv_lab_tests);
        }
    }


    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (this.items.get(position).hasPrescription()) {
            return CHAT_PRESCRIPTION;
        }
        return this.items.get(position).isFromMe() ? CHAT_ME : CHAT_YOU;
    }

    public void insertItem(Message item) {
        this.items.add(item);
        notifyItemInserted(getItemCount());
    }

    public void setItems(List<Message> items) {
        this.items = items;
    }
}