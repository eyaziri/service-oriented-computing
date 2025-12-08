import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormControl, Validators } from '@angular/forms';

export interface ConfirmDialogData {
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  color?: 'primary' | 'accent' | 'warn';
  input?: boolean;
  inputLabel?: string;
  inputValue?: any;
  inputType?: 'text' | 'number' | 'email' | 'password';
  inputRequired?: boolean;
  inputPattern?: string;
  inputPlaceholder?: string;
  hideCancel?: boolean;
}

@Component({
  selector: 'app-confirm-dialog',
  templateUrl: './confirm-dialog.component.html',
  styleUrls: ['./confirm-dialog.component.scss']
})
export class ConfirmDialogComponent implements OnInit {
  inputControl = new FormControl('');
  showInputError = false;
  
  constructor(
    public dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ConfirmDialogData
  ) {}

  ngOnInit() {
    this.data = {
      confirmText: 'Confirmer',
      cancelText: 'Annuler',
      color: 'primary',
      inputType: 'text',
      inputRequired: false,
      hideCancel: false,
      ...this.data
    };

    if (this.data.input) {
      this.inputControl.setValue(this.data.inputValue || '');
      
      if (this.data.inputRequired) {
        this.inputControl.setValidators([Validators.required]);
      }
      
      if (this.data.inputPattern) {
        this.inputControl.setValidators([
          ...this.inputControl.validator ? [this.inputControl.validator] : [],
          Validators.pattern(this.data.inputPattern)
        ]);
      }
      
      this.inputControl.updateValueAndValidity();
    }
  }

  onConfirm(): void {
    if (this.data.input) {
      if (this.inputControl.invalid) {
        this.showInputError = true;
        return;
      }
      this.dialogRef.close(this.inputControl.value);
    } else {
      this.dialogRef.close(true);
    }
  }

  onCancel(): void {
    this.dialogRef.close(this.data.input ? this.inputControl.value : false);
  }

  getInputType(): string {
    return this.data.inputType || 'text';
  }

  getErrorMessage(): string {
    if (this.inputControl.hasError('required')) {
      return 'Ce champ est obligatoire';
    }
    if (this.inputControl.hasError('pattern')) {
      return 'Format invalide';
    }
    return '';
  }
}