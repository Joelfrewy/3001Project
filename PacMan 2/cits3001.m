M = dlmread('data.txt');
N = sortrows(M);
disp(N(:,1));
s = size(N);
P = zeros(s(1)/5, s(2));
for i = 1:5:s(1)
    average = N(i,3);
    average = average + N(i+1,3);
    average = average + N(i+2,3);
    average = average + N(i+3,3);
    average = average + N(i+4,3);
    P(round(i/5)+1,1) = N(i,1);
    P(round(i/5)+1,2) = N(i,2);
    P(round(i/5)+1,3) = average/5;
end
figure;
stem3(M(:,1),M(:,2),M(:,3));