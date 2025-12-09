import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NotificationService } from '../services/notification.service';

export interface NotificationDialogData {
  attractionId: number;
  attractionName: string;
  attractionLocation: string;
}

@Component({
  selector: 'app-notification-dialog',
  templateUrl: './notification-dialog.component.html',
  styleUrls: ['./notification-dialog.component.scss']
})
export class NotificationDialogComponent implements OnInit {
  notificationForm: FormGroup;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private notificationService: NotificationService,
    private snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<NotificationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: NotificationDialogData
  ) {
    this.notificationForm = this.createForm();
  }

  ngOnInit() {
    // Pré-remplir la localisation avec le nom de l'attraction
    this.notificationForm.patchValue({
      location: this.data.attractionLocation
    });
  }

  private createForm(): FormGroup {
    return this.fb.group({
      type: ['CROWD', Validators.required],
      location: ['', Validators.required],
      message: ['', Validators.required],
      severity: [3, [Validators.required, Validators.min(1), Validators.max(5)]]
    });
  }

  displaySeverity(value: number): string {
    const labels = ['Faible', 'Moyenne', 'Haute', 'Critique', 'Urgent'];
    return labels[value - 1] || 'Moyenne';
  }

  getSeverityColor(severity: number): string {
    const colors = ['#4caf50', '#8bc34a', '#ff9800', '#ff5722', '#f44336'];
    return colors[severity - 1] || '#ff9800';
  }

  getSeverityIcon(severity: number): string {
    const icons = ['info', 'warning', 'warning_amber', 'error_outline', 'error'];
    return icons[severity - 1] || 'warning';
  }

  onSubmit(): void {
    if (this.notificationForm.invalid) {
      return;
    }

    this.loading = true;
    const formData = this.notificationForm.value;

    this.notificationService.createAlert(formData).subscribe({
      next: (response) => {
        this.loading = false;
        this.dialogRef.close(true);
        this.snackBar.open('✅ Notification créée avec succès', 'Fermer', {
          duration: 3000
        });
      },
      error: (error) => {
        this.loading = false;
        console.error('Error creating notification:', error);
        this.snackBar.open('❌ Erreur lors de la création', 'Fermer', {
          duration: 3000
        });
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}