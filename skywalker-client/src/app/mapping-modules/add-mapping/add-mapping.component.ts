import { Component, OnInit } from '@angular/core';
import {MappingModule} from "../MappingModule";
import {MappingService} from "../mapping.service";
import {Router} from "@angular/router";
import {SnackbarService} from "../../services/snackbar.service";

@Component({
  selector: 'app-add-mapping',
  templateUrl: './add-mapping.component.html',
  styleUrls: ['./add-mapping.component.css']
})
export class AddMappingComponent implements OnInit {
  selectedFile: File = null;
  moduleContent: string = null;

  constructor(
    private mappingService: MappingService,
    private snackBarService: SnackbarService,
    public router: Router
  ) { }

  ngOnInit() {}

  onModuleFormSubmit(form: MappingModule) {
    this.mappingService.upload(form).subscribe(response => {
      console.log(response);
    });
  }

  onFileSelected(event) {
    this.selectedFile = <File> event.target.files[0];
  }

  onUpload() {
    const fileReader = new FileReader();
    fileReader.onload = (e) => {
      this.moduleContent = (<string>fileReader.result);
      const mappingModule: MappingModule = new MappingModule(this.selectedFile.name, this.moduleContent);
      this.mappingService.upload(mappingModule).subscribe(response => {
        console.log(response);
        this.snackBarService.openSnackBar('Added mapping module', 'close', 2000);
        this.router.navigate(['/mapping-list']);
      });
    };
    fileReader.readAsText(this.selectedFile);
  }
}
