import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportGraph } from './report-graph';

describe('ReportGraph', () => {
  let component: ReportGraph;
  let fixture: ComponentFixture<ReportGraph>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReportGraph]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReportGraph);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
