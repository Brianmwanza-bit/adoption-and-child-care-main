// Example frontend test for a utility function
function multiply(a, b) {
  return a * b;
}
 
test('multiplies 2 * 3 to equal 6', () => {
  expect(multiply(2, 3)).toBe(6);
}); 