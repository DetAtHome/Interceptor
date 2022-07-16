(G-CODE GENERATED BY FLATCAM v8.993 - www.flatcam.org - Version Date: 2020/06/05)

(Name: cnc_socketplateV1.1_drill.txt_mill_cnc)
(Type: G-code from Geometry)
(Units: MM)

(Created on Sunday, 27 June 2021 at 12:00)

(This preprocessor is the default preprocessor used by FlatCAM.)
(It is made to work with MACH3 compatible motion controllers.)

(TOOL DIAMETER: 2.5 mm)
(Feedrate_XY: 120.0 mm/min)
(Feedrate_Z: 60.0 mm/min)
(Feedrate rapids 1500.0 mm/min)

(Z_Cut: -2.4 mm)
(Z_Move: 2.0 mm)
(Z Start: None mm)
(Z End: 15.0 mm)
(Steps per circle: 64)
(Preprocessor Geometry: default)

(X range:    3.3943 ...   65.3355  mm)
(Y range:    3.6458 ...   37.9772  mm)

(Spindle Speed: 1000 RPM)
G21
G90
G94



G01 F120.00
G00 Z2.0000

M03 S1000
G00 X5.0943 Y4.4958
G01 F60.00
G01 Z-2.4000
G01 F120.00
G01 X5.0902 Y4.4125
G01 X5.0780 Y4.3300
G01 X5.0577 Y4.2491
G01 X5.0296 Y4.1705
G01 X4.9940 Y4.0951
G01 X4.9511 Y4.0236
G01 X4.9014 Y3.9566
G01 X4.8454 Y3.8948
G01 X4.7836 Y3.8387
G01 X4.7166 Y3.7891
G01 X4.6450 Y3.7462
G01 X4.5696 Y3.7105
G01 X4.4911 Y3.6824
G01 X4.4102 Y3.6621
G01 X4.3277 Y3.6499
G01 X4.2443 Y3.6458
G01 X4.1610 Y3.6499
G01 X4.0785 Y3.6621
G01 X3.9976 Y3.6824
G01 X3.9191 Y3.7105
G01 X3.8437 Y3.7462
G01 X3.7721 Y3.7891
G01 X3.7051 Y3.8387
G01 X3.6433 Y3.8948
G01 X3.5873 Y3.9566
G01 X3.5376 Y4.0236
G01 X3.4947 Y4.0951
G01 X3.4590 Y4.1705
G01 X3.4309 Y4.2491
G01 X3.4107 Y4.3300
G01 X3.3984 Y4.4125
G01 X3.3943 Y4.4958
G01 X3.3984 Y4.5791
G01 X3.4107 Y4.6616
G01 X3.4309 Y4.7425
G01 X3.4590 Y4.8211
G01 X3.4947 Y4.8965
G01 X3.5376 Y4.9680
G01 X3.5873 Y5.0350
G01 X3.6433 Y5.0968
G01 X3.7051 Y5.1529
G01 X3.7721 Y5.2025
G01 X3.8437 Y5.2454
G01 X3.9191 Y5.2811
G01 X3.9976 Y5.3092
G01 X4.0785 Y5.3295
G01 X4.1610 Y5.3417
G01 X4.2443 Y5.3458
G01 X4.3277 Y5.3417
G01 X4.4102 Y5.3295
G01 X4.4911 Y5.3092
G01 X4.5696 Y5.2811
G01 X4.6450 Y5.2454
G01 X4.7166 Y5.2025
G01 X4.7836 Y5.1529
G01 X4.8454 Y5.0968
G01 X4.9014 Y5.0350
G01 X4.9511 Y4.9680
G01 X4.9940 Y4.8965
G01 X5.0296 Y4.8211
G01 X5.0577 Y4.7425
G01 X5.0780 Y4.6616
G01 X5.0902 Y4.5791
G01 X5.0943 Y4.4958
G00 Z2.0000
G00 X5.0943 Y37.1272
G01 F60.00
G01 Z-2.4000
G01 F120.00
G01 X5.0902 Y37.0439
G01 X5.0780 Y36.9614
G01 X5.0577 Y36.8804
G01 X5.0296 Y36.8019
G01 X4.9940 Y36.7265
G01 X4.9511 Y36.6549
G01 X4.9014 Y36.5879
G01 X4.8454 Y36.5261
G01 X4.7836 Y36.4701
G01 X4.7166 Y36.4204
G01 X4.6450 Y36.3776
G01 X4.5696 Y36.3419
G01 X4.4911 Y36.3138
G01 X4.4102 Y36.2935
G01 X4.3277 Y36.2813
G01 X4.2443 Y36.2772
G01 X4.1610 Y36.2813
G01 X4.0785 Y36.2935
G01 X3.9976 Y36.3138
G01 X3.9191 Y36.3419
G01 X3.8437 Y36.3776
G01 X3.7721 Y36.4204
G01 X3.7051 Y36.4701
G01 X3.6433 Y36.5261
G01 X3.5873 Y36.5879
G01 X3.5376 Y36.6549
G01 X3.4947 Y36.7265
G01 X3.4590 Y36.8019
G01 X3.4309 Y36.8804
G01 X3.4107 Y36.9614
G01 X3.3984 Y37.0439
G01 X3.3943 Y37.1272
G01 X3.3984 Y37.2105
G01 X3.4107 Y37.2930
G01 X3.4309 Y37.3739
G01 X3.4590 Y37.4525
G01 X3.4947 Y37.5279
G01 X3.5376 Y37.5994
G01 X3.5873 Y37.6664
G01 X3.6433 Y37.7282
G01 X3.7051 Y37.7842
G01 X3.7721 Y37.8339
G01 X3.8437 Y37.8768
G01 X3.9191 Y37.9125
G01 X3.9976 Y37.9406
G01 X4.0785 Y37.9608
G01 X4.1610 Y37.9731
G01 X4.2443 Y37.9772
G01 X4.3277 Y37.9731
G01 X4.4102 Y37.9608
G01 X4.4911 Y37.9406
G01 X4.5696 Y37.9125
G01 X4.6450 Y37.8768
G01 X4.7166 Y37.8339
G01 X4.7836 Y37.7842
G01 X4.8454 Y37.7282
G01 X4.9014 Y37.6664
G01 X4.9511 Y37.5994
G01 X4.9940 Y37.5279
G01 X5.0296 Y37.4525
G01 X5.0577 Y37.3739
G01 X5.0780 Y37.2930
G01 X5.0902 Y37.2105
G01 X5.0943 Y37.1272
G00 Z2.0000
G00 X65.3355 Y27.0866
G01 F60.00
G01 Z-2.4000
G01 F120.00
G01 X65.3314 Y27.0032
G01 X65.3192 Y26.9207
G01 X65.2989 Y26.8398
G01 X65.2708 Y26.7613
G01 X65.2351 Y26.6859
G01 X65.1923 Y26.6143
G01 X65.1426 Y26.5473
G01 X65.0866 Y26.4855
G01 X65.0248 Y26.4295
G01 X64.9578 Y26.3798
G01 X64.8862 Y26.3369
G01 X64.8108 Y26.3013
G01 X64.7323 Y26.2732
G01 X64.6513 Y26.2529
G01 X64.5688 Y26.2407
G01 X64.4855 Y26.2366
G01 X64.4022 Y26.2407
G01 X64.3197 Y26.2529
G01 X64.2388 Y26.2732
G01 X64.1602 Y26.3013
G01 X64.0848 Y26.3369
G01 X64.0133 Y26.3798
G01 X63.9463 Y26.4295
G01 X63.8845 Y26.4855
G01 X63.8285 Y26.5473
G01 X63.7788 Y26.6143
G01 X63.7359 Y26.6859
G01 X63.7002 Y26.7613
G01 X63.6721 Y26.8398
G01 X63.6519 Y26.9207
G01 X63.6396 Y27.0032
G01 X63.6355 Y27.0866
G01 X63.6396 Y27.1699
G01 X63.6519 Y27.2524
G01 X63.6721 Y27.3333
G01 X63.7002 Y27.4118
G01 X63.7359 Y27.4872
G01 X63.7788 Y27.5588
G01 X63.8285 Y27.6258
G01 X63.8845 Y27.6876
G01 X63.9463 Y27.7436
G01 X64.0133 Y27.7933
G01 X64.0848 Y27.8362
G01 X64.1602 Y27.8719
G01 X64.2388 Y27.9000
G01 X64.3197 Y27.9202
G01 X64.4022 Y27.9325
G01 X64.4855 Y27.9366
G01 X64.5688 Y27.9325
G01 X64.6513 Y27.9202
G01 X64.7323 Y27.9000
G01 X64.8108 Y27.8719
G01 X64.8862 Y27.8362
G01 X64.9578 Y27.7933
G01 X65.0248 Y27.7436
G01 X65.0866 Y27.6876
G01 X65.1426 Y27.6258
G01 X65.1923 Y27.5588
G01 X65.2351 Y27.4872
G01 X65.2708 Y27.4118
G01 X65.2989 Y27.3333
G01 X65.3192 Y27.2524
G01 X65.3314 Y27.1699
G01 X65.3355 Y27.0866
G00 Z2.0000
G00 X65.3355 Y4.4958
G01 F60.00
G01 Z-2.4000
G01 F120.00
G01 X65.3314 Y4.4125
G01 X65.3192 Y4.3300
G01 X65.2989 Y4.2491
G01 X65.2708 Y4.1705
G01 X65.2351 Y4.0951
G01 X65.1923 Y4.0236
G01 X65.1426 Y3.9566
G01 X65.0866 Y3.8948
G01 X65.0248 Y3.8387
G01 X64.9578 Y3.7891
G01 X64.8862 Y3.7462
G01 X64.8108 Y3.7105
G01 X64.7323 Y3.6824
G01 X64.6513 Y3.6621
G01 X64.5688 Y3.6499
G01 X64.4855 Y3.6458
G01 X64.4022 Y3.6499
G01 X64.3197 Y3.6621
G01 X64.2388 Y3.6824
G01 X64.1602 Y3.7105
G01 X64.0848 Y3.7462
G01 X64.0133 Y3.7891
G01 X63.9463 Y3.8387
G01 X63.8845 Y3.8948
G01 X63.8285 Y3.9566
G01 X63.7788 Y4.0236
G01 X63.7359 Y4.0951
G01 X63.7002 Y4.1705
G01 X63.6721 Y4.2491
G01 X63.6519 Y4.3300
G01 X63.6396 Y4.4125
G01 X63.6355 Y4.4958
G01 X63.6396 Y4.5791
G01 X63.6519 Y4.6616
G01 X63.6721 Y4.7425
G01 X63.7002 Y4.8211
G01 X63.7359 Y4.8965
G01 X63.7788 Y4.9680
G01 X63.8285 Y5.0350
G01 X63.8845 Y5.0968
G01 X63.9463 Y5.1529
G01 X64.0133 Y5.2025
G01 X64.0848 Y5.2454
G01 X64.1602 Y5.2811
G01 X64.2388 Y5.3092
G01 X64.3197 Y5.3295
G01 X64.4022 Y5.3417
G01 X64.4855 Y5.3458
G01 X64.5688 Y5.3417
G01 X64.6513 Y5.3295
G01 X64.7323 Y5.3092
G01 X64.8108 Y5.2811
G01 X64.8862 Y5.2454
G01 X64.9578 Y5.2025
G01 X65.0248 Y5.1529
G01 X65.0866 Y5.0968
G01 X65.1426 Y5.0350
G01 X65.1923 Y4.9680
G01 X65.2351 Y4.8965
G01 X65.2708 Y4.8211
G01 X65.2989 Y4.7425
G01 X65.3192 Y4.6616
G01 X65.3314 Y4.5791
G01 X65.3355 Y4.4958
G00 Z2.0000
M05
G00 Z2.0000
G00 Z15.00
