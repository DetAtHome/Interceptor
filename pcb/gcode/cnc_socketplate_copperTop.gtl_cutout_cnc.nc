(G-CODE GENERATED BY FLATCAM v8.993 - www.flatcam.org - Version Date: 2020/06/05)

(Name: cnc_socketplate_copperTop.gtl_cutout_cnc)
(Type: G-code from Geometry)
(Units: MM)

(Created on Saturday, 26 June 2021 at 11:03)

(This preprocessor is the default preprocessor used by FlatCAM.)
(It is made to work with MACH3 compatible motion controllers.)

(TOOL DIAMETER: 2.4 mm)
(Feedrate_XY: 120.0 mm/min)
(Feedrate_Z: 60.0 mm/min)
(Feedrate rapids 1500.0 mm/min)

(Z_Cut: -1.8 mm)
(DepthPerCut: 0.6 mm <=>3 passes)
(Z_Move: 2.0 mm)
(Z Start: None mm)
(Z End: 15.0 mm)
(Steps per circle: 64)
(Preprocessor Geometry: default)

(X range:   -2.8849 ...   89.0924  mm)
(Y range:   -0.3449 ...   56.5042  mm)

(Spindle Speed: 1000 RPM)
G21
G90
G94



G01 F120.00
G00 Z2.0000

M03 S1000
G00 X78.8924 Y-0.3449
G01 F60.00
G01 Z-0.6000
G01 F120.00
G01 X7.3151 Y-0.3449
G01 X6.3153 Y-0.2958
G01 X5.3251 Y-0.1490
G01 X4.3541 Y0.0943
G01 X3.4117 Y0.4315
G01 X2.5068 Y0.8595
G01 X1.6482 Y1.3741
G01 X0.8442 Y1.9703
G01 X0.1026 Y2.6426
G01 X-0.5697 Y3.3842
G01 X-1.1659 Y4.1882
G01 X-1.6805 Y5.0468
G01 X-2.1085 Y5.9517
G01 X-2.4457 Y6.8941
G01 X-2.6890 Y7.8651
G01 X-2.8358 Y8.8553
G01 X-2.8849 Y9.8551
G01 X-2.8849 Y46.3042
G01 X-2.8358 Y47.3040
G01 X-2.6890 Y48.2941
G01 X-2.4457 Y49.2651
G01 X-2.1085 Y50.2076
G01 X-1.6805 Y51.1124
G01 X-1.1659 Y51.9710
G01 X-0.5697 Y52.7750
G01 X0.1026 Y53.5167
G01 X0.8442 Y54.1889
G01 X1.6482 Y54.7852
G01 X2.5068 Y55.2998
G01 X3.4117 Y55.7278
G01 X4.3541 Y56.0650
G01 X5.3251 Y56.3082
G01 X6.3153 Y56.4551
G01 X7.3151 Y56.5042
G01 X78.8924 Y56.5042
G01 X79.8922 Y56.4551
G01 X80.8823 Y56.3082
G01 X81.8533 Y56.0650
G01 X82.7958 Y55.7278
G01 X83.7006 Y55.2998
G01 X84.5592 Y54.7852
G01 X85.3632 Y54.1889
G01 X86.1049 Y53.5167
G01 X86.7771 Y52.7750
G01 X87.3734 Y51.9710
G01 X87.8880 Y51.1124
G01 X88.3160 Y50.2076
G01 X88.6532 Y49.2651
G01 X88.8964 Y48.2941
G01 X89.0433 Y47.3040
G01 X89.0924 Y46.3042
G01 X89.0924 Y9.8551
G01 X89.0433 Y8.8553
G01 X88.8964 Y7.8651
G01 X88.6532 Y6.8941
G01 X88.3160 Y5.9517
G01 X87.8880 Y5.0468
G01 X87.3734 Y4.1882
G01 X86.7771 Y3.3842
G01 X86.1049 Y2.6426
G01 X85.3632 Y1.9703
G01 X84.5592 Y1.3741
G01 X83.7006 Y0.8595
G01 X82.7958 Y0.4315
G01 X81.8533 Y0.0943
G01 X80.8823 Y-0.1490
G01 X79.8922 Y-0.2958
G01 X78.8924 Y-0.3449
G00 X78.8924 Y-0.3449
G01 F60.00
G01 Z-1.2000
G01 F120.00
G01 X7.3151 Y-0.3449
G01 X6.3153 Y-0.2958
G01 X5.3251 Y-0.1490
G01 X4.3541 Y0.0943
G01 X3.4117 Y0.4315
G01 X2.5068 Y0.8595
G01 X1.6482 Y1.3741
G01 X0.8442 Y1.9703
G01 X0.1026 Y2.6426
G01 X-0.5697 Y3.3842
G01 X-1.1659 Y4.1882
G01 X-1.6805 Y5.0468
G01 X-2.1085 Y5.9517
G01 X-2.4457 Y6.8941
G01 X-2.6890 Y7.8651
G01 X-2.8358 Y8.8553
G01 X-2.8849 Y9.8551
G01 X-2.8849 Y46.3042
G01 X-2.8358 Y47.3040
G01 X-2.6890 Y48.2941
G01 X-2.4457 Y49.2651
G01 X-2.1085 Y50.2076
G01 X-1.6805 Y51.1124
G01 X-1.1659 Y51.9710
G01 X-0.5697 Y52.7750
G01 X0.1026 Y53.5167
G01 X0.8442 Y54.1889
G01 X1.6482 Y54.7852
G01 X2.5068 Y55.2998
G01 X3.4117 Y55.7278
G01 X4.3541 Y56.0650
G01 X5.3251 Y56.3082
G01 X6.3153 Y56.4551
G01 X7.3151 Y56.5042
G01 X78.8924 Y56.5042
G01 X79.8922 Y56.4551
G01 X80.8823 Y56.3082
G01 X81.8533 Y56.0650
G01 X82.7958 Y55.7278
G01 X83.7006 Y55.2998
G01 X84.5592 Y54.7852
G01 X85.3632 Y54.1889
G01 X86.1049 Y53.5167
G01 X86.7771 Y52.7750
G01 X87.3734 Y51.9710
G01 X87.8880 Y51.1124
G01 X88.3160 Y50.2076
G01 X88.6532 Y49.2651
G01 X88.8964 Y48.2941
G01 X89.0433 Y47.3040
G01 X89.0924 Y46.3042
G01 X89.0924 Y9.8551
G01 X89.0433 Y8.8553
G01 X88.8964 Y7.8651
G01 X88.6532 Y6.8941
G01 X88.3160 Y5.9517
G01 X87.8880 Y5.0468
G01 X87.3734 Y4.1882
G01 X86.7771 Y3.3842
G01 X86.1049 Y2.6426
G01 X85.3632 Y1.9703
G01 X84.5592 Y1.3741
G01 X83.7006 Y0.8595
G01 X82.7958 Y0.4315
G01 X81.8533 Y0.0943
G01 X80.8823 Y-0.1490
G01 X79.8922 Y-0.2958
G01 X78.8924 Y-0.3449
G00 X78.8924 Y-0.3449
G01 F60.00
G01 Z-1.8000
G01 F120.00
G01 X7.3151 Y-0.3449
G01 X6.3153 Y-0.2958
G01 X5.3251 Y-0.1490
G01 X4.3541 Y0.0943
G01 X3.4117 Y0.4315
G01 X2.5068 Y0.8595
G01 X1.6482 Y1.3741
G01 X0.8442 Y1.9703
G01 X0.1026 Y2.6426
G01 X-0.5697 Y3.3842
G01 X-1.1659 Y4.1882
G01 X-1.6805 Y5.0468
G01 X-2.1085 Y5.9517
G01 X-2.4457 Y6.8941
G01 X-2.6890 Y7.8651
G01 X-2.8358 Y8.8553
G01 X-2.8849 Y9.8551
G01 X-2.8849 Y46.3042
G01 X-2.8358 Y47.3040
G01 X-2.6890 Y48.2941
G01 X-2.4457 Y49.2651
G01 X-2.1085 Y50.2076
G01 X-1.6805 Y51.1124
G01 X-1.1659 Y51.9710
G01 X-0.5697 Y52.7750
G01 X0.1026 Y53.5167
G01 X0.8442 Y54.1889
G01 X1.6482 Y54.7852
G01 X2.5068 Y55.2998
G01 X3.4117 Y55.7278
G01 X4.3541 Y56.0650
G01 X5.3251 Y56.3082
G01 X6.3153 Y56.4551
G01 X7.3151 Y56.5042
G01 X78.8924 Y56.5042
G01 X79.8922 Y56.4551
G01 X80.8823 Y56.3082
G01 X81.8533 Y56.0650
G01 X82.7958 Y55.7278
G01 X83.7006 Y55.2998
G01 X84.5592 Y54.7852
G01 X85.3632 Y54.1889
G01 X86.1049 Y53.5167
G01 X86.7771 Y52.7750
G01 X87.3734 Y51.9710
G01 X87.8880 Y51.1124
G01 X88.3160 Y50.2076
G01 X88.6532 Y49.2651
G01 X88.8964 Y48.2941
G01 X89.0433 Y47.3040
G01 X89.0924 Y46.3042
G01 X89.0924 Y9.8551
G01 X89.0433 Y8.8553
G01 X88.8964 Y7.8651
G01 X88.6532 Y6.8941
G01 X88.3160 Y5.9517
G01 X87.8880 Y5.0468
G01 X87.3734 Y4.1882
G01 X86.7771 Y3.3842
G01 X86.1049 Y2.6426
G01 X85.3632 Y1.9703
G01 X84.5592 Y1.3741
G01 X83.7006 Y0.8595
G01 X82.7958 Y0.4315
G01 X81.8533 Y0.0943
G01 X80.8823 Y-0.1490
G01 X79.8922 Y-0.2958
G01 X78.8924 Y-0.3449
G00 Z2.0000
M05
G00 Z2.0000
G00 Z15.00
